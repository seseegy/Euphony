package euphony.app.playback.queues

import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import euphony.app.db.entities.AlbumWithSongs
import euphony.app.extensions.toMediaItem
import euphony.app.innertube.YouTube
import euphony.app.innertube.models.WatchEndpoint
import euphony.app.models.MediaMetadata
import euphony.app.playback.withQueueEntry
import euphony.app.innertube.pages.QueueChip

class LocalAlbumRadio(
    internal val albumWithSongs: AlbumWithSongs,
    internal val startIndex: Int = 0,
) : Queue {
    override val preloadItem: MediaMetadata? = null
    override val recommendationOnlyContinuation: Boolean = true
    override var queueChips: List<QueueChip> = emptyList()
        private set

    private lateinit var playlistId: String
    private val endpoint: WatchEndpoint
        get() =
            WatchEndpoint(
                playlistId = playlistId,
                params = "wAEB",
            )

    private val localOnly = albumWithSongs.album.isLocal || albumWithSongs.songs.all { it.song.isLocal }
    private val initialSongIds = albumWithSongs.songs.map { it.id }.toSet()
    private var continuation: String? = null
    private var firstTimeLoaded: Boolean = false

    override suspend fun getInitialStatus(): Queue.Status =
        withContext(IO) {
            Queue.Status(
                title = albumWithSongs.album.title,
                items = albumWithSongs.songs.map { it.toMediaItem() },
                mediaItemIndex = startIndex,
            )
        }

    override fun hasNextPage(): Boolean = !localOnly && (!firstTimeLoaded || continuation != null)

    override suspend fun nextPage(): List<MediaItem> =
        withContext(IO) {
            if (localOnly) return@withContext emptyList()
            if (!firstTimeLoaded) {
                playlistId =
                    YouTube
                        .album(albumWithSongs.album.id)
                        .getOrThrow()
                        .album.playlistId
                val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
                queueChips = nextResult.queueChips
                continuation = nextResult.continuation
                firstTimeLoaded = true
                return@withContext nextResult.items
                    .filterNot { it.id in initialSongIds }
                    .map { it.toMediaItem().withQueueEntry(recommended = true) }
            }
            val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
            if (nextResult.queueChips.isNotEmpty()) queueChips = nextResult.queueChips
            continuation = nextResult.continuation
            nextResult.items.map { it.toMediaItem().withQueueEntry(recommended = true) }
        }
}

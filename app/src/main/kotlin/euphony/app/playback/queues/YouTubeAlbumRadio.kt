package euphony.app.playback.queues

import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import euphony.app.extensions.toMediaItem
import euphony.app.innertube.YouTube
import euphony.app.innertube.models.WatchEndpoint
import euphony.app.models.MediaMetadata
import euphony.app.playback.withQueueEntry
import euphony.app.innertube.pages.QueueChip

class YouTubeAlbumRadio(
    internal val playlistId: String,
) : Queue {
    override val preloadItem: MediaMetadata? = null
    override val recommendationOnlyContinuation: Boolean = true
    override var queueChips: List<QueueChip> = emptyList()
        private set

    private val endpoint: WatchEndpoint
        get() =
            WatchEndpoint(
                playlistId = playlistId,
                params = "wAEB",
            )

    internal var albumSongCount = 0
    internal var continuation: String? = null
    internal var firstTimeLoaded: Boolean = false

    internal constructor(
        playlistId: String,
        albumSongCount: Int,
        continuation: String?,
        firstTimeLoaded: Boolean,
    ) : this(playlistId) {
        this.albumSongCount = albumSongCount
        this.continuation = continuation
        this.firstTimeLoaded = firstTimeLoaded
    }

    override suspend fun getInitialStatus(): Queue.Status =
        withContext(IO) {
            val albumSongs = YouTube.albumSongs(playlistId).getOrThrow()
            albumSongCount = albumSongs.size
            Queue.Status(
                title =
                    albumSongs
                        .first()
                        .album
                        ?.name
                        .orEmpty(),
                items = albumSongs.map { it.toMediaItem() },
                mediaItemIndex = 0,
            )
        }

    override fun hasNextPage(): Boolean = !firstTimeLoaded || continuation != null

    override suspend fun nextPage(): List<MediaItem> =
        withContext(IO) {
            val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
            if (nextResult.queueChips.isNotEmpty()) queueChips = nextResult.queueChips
            continuation = nextResult.continuation
            if (!firstTimeLoaded) {
                firstTimeLoaded = true
                nextResult.items.drop(albumSongCount).map { it.toMediaItem().withQueueEntry(recommended = true) }
            } else {
                nextResult.items.map { it.toMediaItem().withQueueEntry(recommended = true) }
            }
        }
}

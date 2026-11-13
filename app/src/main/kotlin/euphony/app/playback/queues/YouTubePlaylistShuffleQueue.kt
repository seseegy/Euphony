package euphony.app.playback.queues

import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import euphony.app.extensions.toMediaItem
import euphony.app.innertube.YouTube
import euphony.app.innertube.utils.completed
import euphony.app.models.MediaMetadata

internal class YouTubePlaylistShuffleQueue(
    private val playlistId: String,
) : Queue {
    override val preloadItem: MediaMetadata? = null

    override suspend fun getInitialStatus(): Queue.Status =
        withContext(IO) {
            val page = YouTube.playlist(playlistId).completed().getOrThrow()
            Queue.Status(
                title = page.playlist.title,
                items = page.songs.shuffled().map { it.toMediaItem() },
                mediaItemIndex = 0,
            )
        }

    override fun hasNextPage(): Boolean = false

    override suspend fun nextPage(): List<MediaItem> = throw UnsupportedOperationException()
}

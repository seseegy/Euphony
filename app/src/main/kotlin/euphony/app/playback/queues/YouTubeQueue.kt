package euphony.app.playback.queues

import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import euphony.app.extensions.toMediaItem
import euphony.app.innertube.YouTube
import euphony.app.innertube.models.WatchEndpoint
import euphony.app.models.MediaMetadata
import euphony.app.innertube.pages.QueueChip
import euphony.app.innertube.pages.NextResult
import euphony.app.playback.withQueueEntry

class YouTubeQueue(
    internal var endpoint: WatchEndpoint,
    override val preloadItem: MediaMetadata? = null,
    internal val followAutomixPreview: Boolean = false,
    private val expandToFullQueueWhenAutoLoadMoreDisabled: Boolean = false,
    initialContinuation: String? = null,
) : Queue {
    internal var continuation: String? = initialContinuation
        private set
    override val isRadio: Boolean = followAutomixPreview || endpoint.playlistId?.startsWith("RD") == true
    var chips: List<QueueChip> = emptyList()
        private set
    override val queueChips: List<QueueChip> get() = chips

    override suspend fun getInitialStatus(): Queue.Status {
        val nextResult =
            withContext(IO) {
                YouTube
                    .next(
                        endpoint = endpoint,
                        continuation = continuation,
                        followAutomixPreview = followAutomixPreview,
                    ).getOrThrow()
            }
        endpoint = nextResult.endpoint
        continuation = nextResult.continuation
        chips = nextResult.queueChips
        return Queue.Status(
            title = nextResult.title,
            items = nextResult.items.map { it.toMediaItem().withQueueEntry(recommended = isRadio) },
            mediaItemIndex = nextResult.currentIndex ?: 0,
        )
    }

    override fun hasNextPage(): Boolean = continuation != null

    override fun shouldExpandToFullQueueWhenAutoLoadMoreDisabled(): Boolean = expandToFullQueueWhenAutoLoadMoreDisabled

    override suspend fun nextPage(): List<MediaItem> {
        val nextResult = fetchNextPage()
        acceptNextPage(nextResult)
        return nextResult.items.map { it.toMediaItem().withQueueEntry(recommended = isRadio) }
    }

    internal suspend fun fetchNextPage(): NextResult = withContext(IO) {
        YouTube.next(endpoint = endpoint, continuation = continuation, followAutomixPreview = followAutomixPreview).getOrThrow()
    }

    internal fun updateChips(values: List<QueueChip>) {
        chips = values
    }

    internal fun acceptNextPage(nextResult: NextResult) {
        endpoint = nextResult.endpoint
        continuation = nextResult.continuation
        if (nextResult.queueChips.isNotEmpty()) chips = nextResult.queueChips
    }

    companion object {
        fun shuffledPlaylist(
            playlistId: String,
            endpoint: WatchEndpoint,
        ): Queue {
            val normalizedId = playlistId.removePrefix("VL")
            return if (normalizedId.startsWith("RD")) {
                playlist(endpoint)
            } else {
                YouTubePlaylistShuffleQueue(normalizedId)
            }
        }

        fun playlist(
            endpoint: WatchEndpoint,
            preloadItem: MediaMetadata? = null,
        ) = YouTubeQueue(
            endpoint = endpoint,
            preloadItem = preloadItem,
            expandToFullQueueWhenAutoLoadMoreDisabled = true,
        )

        fun radio(song: MediaMetadata) =
            YouTubeQueue(
                endpoint = WatchEndpoint(videoId = song.id),
                preloadItem = song,
                followAutomixPreview = true,
            )
    }
}

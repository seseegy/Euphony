package euphony.app.extensions

import euphony.app.innertube.models.WatchEndpoint
import euphony.app.models.MediaMetadata
import euphony.app.models.PersistQueue
import euphony.app.models.QueueData
import euphony.app.models.QueueType
import euphony.app.playback.queues.ListQueue
import euphony.app.playback.queues.LocalAlbumRadio
import euphony.app.playback.queues.Queue
import euphony.app.playback.queues.YouTubeAlbumRadio
import euphony.app.playback.queues.YouTubeQueue
import euphony.app.playback.withQueueEntry

fun Queue.toPersistQueue(
    title: String?,
    items: List<MediaMetadata>,
    mediaItemIndex: Int,
    position: Long,
): PersistQueue =
    when (this) {
        is ListQueue -> {
            PersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                queueType = QueueType.LIST,
            )
        }

        is YouTubeQueue -> {
            PersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                queueType = QueueType.YOUTUBE,
                queueData =
                    QueueData.YouTubeData(
                        videoId = endpoint.videoId,
                        playlistId = endpoint.playlistId,
                        endpointParams = endpoint.params,
                        followAutomixPreview = followAutomixPreview,
                        continuation = continuation,
                    ),
            )
        }

        is YouTubeAlbumRadio -> {
            PersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                queueType = QueueType.YOUTUBE_ALBUM_RADIO,
                queueData =
                    QueueData.YouTubeAlbumRadioData(
                        playlistId = playlistId,
                        albumSongCount = albumSongCount,
                        continuation = continuation,
                        firstTimeLoaded = firstTimeLoaded,
                    ),
            )
        }

        is LocalAlbumRadio -> {
            PersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                queueType = QueueType.LOCAL_ALBUM_RADIO,
                queueData =
                    QueueData.LocalAlbumRadioData(
                        albumId = albumWithSongs.album.id,
                        startIndex = startIndex,
                    ),
            )
        }

        else -> {
            PersistQueue(
                title = title,
                items = items,
                mediaItemIndex = mediaItemIndex,
                position = position,
                queueType = QueueType.LIST,
            )
        }
    }

fun PersistQueue.toQueue(): Queue {
    val identities = mutableSetOf<String>()
    val restored = items.mapIndexed { index, item ->
        val entry = entryMetadata?.getOrNull(index)
        val id = entry?.id?.takeIf { it.isNotBlank() && identities.add(it) } ?: java.util.UUID.randomUUID().toString().also(identities::add)
        item.toMediaItem().withQueueEntry(recommended = entry?.recommended == true, id = id)
    }
    return ListQueue(title = title, items = restored, startIndex = mediaItemIndex, position = position)
}

fun PersistQueue.toContinuationQueue(): Queue =
    when (queueType) {
        is QueueType.LIST -> {
            ListQueue(
                title = title,
                items = items.map { it.toMediaItem() },
                startIndex = mediaItemIndex,
                position = position,
            )
        }

        is QueueType.YOUTUBE -> {
            val data =
                queueData as? QueueData.YouTubeData
                    ?: return ListQueue(title, items.map { it.toMediaItem() }, mediaItemIndex, position)
            YouTubeQueue(
                endpoint =
                    WatchEndpoint(
                        videoId = data.videoId,
                        playlistId = data.playlistId,
                        params = data.endpointParams,
                    ),
                followAutomixPreview = data.followAutomixPreview,
                initialContinuation = data.continuation,
            )
        }

        is QueueType.YOUTUBE_ALBUM_RADIO -> {
            val data =
                queueData as? QueueData.YouTubeAlbumRadioData
                    ?: return ListQueue(title, items.map { it.toMediaItem() }, mediaItemIndex, position)
            YouTubeAlbumRadio(
                playlistId = data.playlistId,
                albumSongCount = data.albumSongCount,
                continuation = data.continuation,
                firstTimeLoaded = data.firstTimeLoaded,
            )
        }

        is QueueType.LOCAL_ALBUM_RADIO -> {
            ListQueue(
                title = title,
                items = items.map { it.toMediaItem() },
                startIndex = mediaItemIndex,
                position = position,
            )
        }
    }

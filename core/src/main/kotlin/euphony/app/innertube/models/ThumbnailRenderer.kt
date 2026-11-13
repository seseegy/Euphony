package euphony.app.innertube.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ThumbnailRenderer(
    @JsonNames("croppedSquareThumbnailRenderer")
    val musicThumbnailRenderer: MusicThumbnailRenderer? = null,
    val musicAnimatedThumbnailRenderer: MusicAnimatedThumbnailRenderer? = null,
    val croppedSquareThumbnailRenderer: MusicThumbnailRenderer? = null,
) {
    @Serializable
    data class MusicThumbnailRenderer(
        val thumbnail: Thumbnails,
        val thumbnailCrop: String?,
        val thumbnailScale: String?,
    ) {
        fun getBestThumbnail(): Thumbnail? {
            val thumbnails = thumbnail.thumbnails
            return thumbnails
                .filter { candidate ->
                    candidate.normalizedUrl.isNotBlank() &&
                        (candidate.width ?: 0) > 0 &&
                        (candidate.height ?: 0) > 0
                }.maxByOrNull { candidate ->
                    val width = candidate.width ?: 0
                    val height = candidate.height ?: 0
                    width.toLong() * height.toLong()
                } ?: thumbnails.lastOrNull { it.normalizedUrl.isNotBlank() }
        }

        fun getThumbnailUrl() = getBestThumbnail()?.normalizedUrl
    }

    @Serializable
    data class MusicAnimatedThumbnailRenderer(
        val animatedThumbnail: Thumbnails,
        val backupRenderer: MusicThumbnailRenderer,
    )
}

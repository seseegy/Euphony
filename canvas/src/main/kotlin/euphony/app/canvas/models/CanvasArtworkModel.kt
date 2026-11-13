package euphony.app.canvas.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import euphony.app.canvas.CanvasSource

@Serializable
data class CanvasArtwork(
    val source: CanvasSource? = null,
    val name: String? = null,
    val artist: String? = null,
    @SerialName("albumId")
    val albumId: String? = null,
    val albumName: String? = null,
    val static: String? = null,
    val animated: String? = null,
    val animatedVertical: String? = null,
    val videoUrl: String? = null,
    val videoUrlVertical: String? = null,
) {
    val preferredAnimationUrl: String?
        get() = animated ?: videoUrl

    val preferredVerticalAnimationUrl: String?
        get() = animatedVertical ?: videoUrlVertical
}

package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class LikeBody(
    val context: Context,
    val target: Target,
) {
    @Serializable
    sealed class Target {
        @Serializable
        data class VideoTarget(
            val videoId: String,
        ) : Target()

        @Serializable
        data class PlaylistTarget(
            val playlistId: String,
        ) : Target()
    }
}

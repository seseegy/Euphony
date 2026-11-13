package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class PlaylistDeleteBody(
    val context: Context,
    val playlistId: String,
)

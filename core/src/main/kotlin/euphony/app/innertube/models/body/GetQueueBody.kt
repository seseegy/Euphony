package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class GetQueueBody(
    val context: Context,
    val videoIds: List<String>?,
    val playlistId: String?,
)

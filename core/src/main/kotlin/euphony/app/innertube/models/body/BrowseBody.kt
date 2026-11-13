package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
    val continuation: String?,
)

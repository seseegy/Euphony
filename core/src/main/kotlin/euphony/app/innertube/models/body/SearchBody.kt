package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class SearchBody(
    val context: Context,
    val query: String?,
    val params: String?,
)

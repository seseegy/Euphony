package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)

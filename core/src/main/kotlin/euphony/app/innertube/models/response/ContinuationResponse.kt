package euphony.app.innertube.models.response

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.MusicShelfRenderer

@Serializable
data class ContinuationResponse(
    val onResponseReceivedActions: List<ResponseAction>?,
) {
    @Serializable
    data class ResponseAction(
        val appendContinuationItemsAction: ContinuationItems?,
    )

    @Serializable
    data class ContinuationItems(
        val continuationItems: List<MusicShelfRenderer.Content>?,
    )
}

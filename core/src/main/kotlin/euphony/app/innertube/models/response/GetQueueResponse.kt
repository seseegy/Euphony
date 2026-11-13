package euphony.app.innertube.models.response

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.PlaylistPanelRenderer

@Serializable
data class GetQueueResponse(
    val queueDatas: List<QueueData>,
) {
    @Serializable
    data class QueueData(
        val content: PlaylistPanelRenderer.Content,
    )
}

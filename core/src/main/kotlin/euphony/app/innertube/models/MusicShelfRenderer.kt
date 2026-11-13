package euphony.app.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class MusicShelfRenderer(
    val title: Runs? = null,
    val contents: List<Content>? = null,
    val continuations: List<Continuation>? = null,
    val bottomEndpoint: NavigationEndpoint? = null,
    val moreContentButton: Button? = null,
) {
    @Serializable
    data class Content(
        val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer? = null,
        val musicMultiRowListItemRenderer: MusicMultiRowListItemRenderer? = null,
        val continuationItemRenderer: ContinuationItemRenderer? = null,
    )
}

fun List<MusicShelfRenderer.Content>.getItems(): List<MusicResponsiveListItemRenderer> = mapNotNull { it.musicResponsiveListItemRenderer }

fun List<MusicShelfRenderer.Content>.getContinuation(): String? =
    firstOrNull { it.continuationItemRenderer != null }
        ?.continuationItemRenderer
        ?.continuationEndpoint
        ?.continuationCommand
        ?.token

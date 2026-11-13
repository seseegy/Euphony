package euphony.app.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class MusicMultiRowListItemRenderer(
    val title: Runs? = null,
    val subtitle: Runs? = null,
    val description: Runs? = null,
    val thumbnail: ThumbnailRenderer? = null,
    val onTap: NavigationEndpoint? = null,
    val overlay: MusicResponsiveListItemRenderer.Overlay? = null,
    val playbackProgress: PlaybackProgress? = null,
) {
    @Serializable
    data class PlaybackProgress(
        val musicPlaybackProgressRenderer: MusicPlaybackProgressRenderer? = null,
    ) {
        @Serializable
        data class MusicPlaybackProgressRenderer(
            val durationText: Runs? = null,
            val playbackProgressPercentage: Int? = null,
        )
    }
}

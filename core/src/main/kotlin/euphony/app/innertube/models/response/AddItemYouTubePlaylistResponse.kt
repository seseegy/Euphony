package euphony.app.innertube.models.response

import kotlinx.serialization.Serializable

@Serializable
public data class AddItemYouTubePlaylistResponse(
    public val status: String,
    public val playlistEditResults: List<PlaylistEditResult> = emptyList(),
) {
    @Serializable
    public data class PlaylistEditResult(
        public val playlistEditVideoAddedResultData: PlaylistEditVideoAddedResultData? = null,
    ) {
        @Serializable
        public data class PlaylistEditVideoAddedResultData(
            public val setVideoId: String? = null,
            public val videoId: String? = null,
        )
    }
}

package euphony.app.innertube.pages

import euphony.app.innertube.models.Album
import euphony.app.innertube.models.Artist
import euphony.app.innertube.models.BrowseEndpoint
import euphony.app.innertube.models.PlaylistPanelVideoRenderer
import euphony.app.innertube.models.SongItem
import euphony.app.innertube.models.WatchEndpoint
import euphony.app.innertube.models.oddElements
import euphony.app.innertube.models.splitBySeparator
import euphony.app.innertube.utils.parseTime

data class NextResult(
    val title: String? = null,
    val items: List<SongItem>,
    val currentIndex: Int? = null,
    val lyricsEndpoint: BrowseEndpoint? = null,
    val relatedEndpoint: BrowseEndpoint? = null,
    val continuation: String?,
    val endpoint: WatchEndpoint, // current or continuation next endpoint
    val queueChips: List<QueueChip> = emptyList(),
)

data class QueueChip(
    val id: String,
    val title: String,
    val selected: Boolean,
    val endpoint: WatchEndpoint,
    val deselectEndpoint: WatchEndpoint? = null,
)

object NextPage {
    fun fromPlaylistPanelVideoRenderer(renderer: PlaylistPanelVideoRenderer): SongItem? {
        val longByLineRuns = renderer.longBylineText?.runs?.splitBySeparator() ?: return null
        val thumbnail = renderer.thumbnail.thumbnails.lastOrNull() ?: return null
        return SongItem(
            id = renderer.videoId ?: return null,
            title =
                renderer.title
                    ?.runs
                    ?.firstOrNull()
                    ?.text ?: return null,
            artists =
                longByLineRuns.firstOrNull()?.oddElements()?.map {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId,
                    )
                } ?: return null,
            album =
                longByLineRuns
                    .getOrNull(1)
                    ?.firstOrNull()
                    ?.takeIf {
                        it.navigationEndpoint?.browseEndpoint != null
                    }?.let {
                        Album(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId!!,
                        )
                    },
            duration =
                renderer.lengthText
                    ?.runs
                    ?.firstOrNull()
                    ?.text
                    ?.parseTime() ?: return null,
            thumbnail = thumbnail.normalizedUrl,
            thumbnailWidth = thumbnail.width,
            thumbnailHeight = thumbnail.height,
            explicit =
                renderer.badges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null,
            endpoint = renderer.navigationEndpoint.anyWatchEndpoint,
        )
    }
}

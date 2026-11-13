package euphony.app.innertube.pages

import euphony.app.innertube.models.AlbumItem
import euphony.app.innertube.models.MusicTwoRowItemRenderer

data class LibraryAlbumsPage(
    val albums: List<AlbumItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): AlbumItem? {
            val thumbnail = renderer.thumbnailRenderer.musicThumbnailRenderer?.getBestThumbnail() ?: return null
            val browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null
            val playlistId =
                renderer.thumbnailOverlay
                    ?.musicItemThumbnailOverlayRenderer
                    ?.content
                    ?.musicPlayButtonRenderer
                    ?.playNavigationEndpoint
                    ?.watchPlaylistEndpoint
                    ?.playlistId
                    ?: renderer.menu
                        ?.menuRenderer
                        ?.items
                        ?.firstOrNull()
                        ?.menuNavigationItemRenderer
                        ?.navigationEndpoint
                        ?.watchPlaylistEndpoint
                        ?.playlistId
                    ?: browseId.removePrefix("MPREb_").let { "OLAK5uy_$it" }

            return AlbumItem(
                browseId = browseId,
                playlistId = playlistId,
                title =
                    renderer.title.runs
                        ?.firstOrNull()
                        ?.text ?: return null,
                artists = null,
                year =
                    renderer.subtitle
                        ?.runs
                        ?.lastOrNull()
                        ?.text
                        ?.toIntOrNull(),
                thumbnail = thumbnail.normalizedUrl,
                thumbnailWidth = thumbnail.width,
                thumbnailHeight = thumbnail.height,
                explicit =
                    renderer.subtitleBadges?.find {
                        it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                    } != null,
            )
        }
    }
}

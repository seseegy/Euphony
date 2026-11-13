package euphony.app.extensions

import euphony.app.innertube.models.AlbumItem
import euphony.app.innertube.models.ArtistItem
import euphony.app.innertube.models.EpisodeItem
import euphony.app.innertube.models.PlaylistItem
import euphony.app.innertube.models.PodcastItem
import euphony.app.innertube.models.SongItem
import euphony.app.innertube.models.YTItem
import euphony.app.innertube.pages.BrowseResult

fun <T : YTItem> List<T>.filterBlockedArtists(blockedArtistIds: Set<String>): List<T> {
    if (blockedArtistIds.isEmpty()) return this

    return filter { item ->
        when (item) {
            is ArtistItem -> item.id !in blockedArtistIds
            is SongItem -> item.artists.none { it.id in blockedArtistIds }
            is AlbumItem -> item.artists.orEmpty().none { it.id in blockedArtistIds }
            is PlaylistItem -> item.author?.id !in blockedArtistIds
            is PodcastItem -> item.author?.id !in blockedArtistIds
            is EpisodeItem -> item.podcast?.id !in blockedArtistIds
        }
    }
}

fun BrowseResult.filterBlockedArtists(blockedArtistIds: Set<String>): BrowseResult {
    if (blockedArtistIds.isEmpty()) return this

    return copy(
        items =
            items.mapNotNull { section ->
                section.copy(
                    items =
                        section.items
                            .filterBlockedArtists(blockedArtistIds)
                            .ifEmpty { return@mapNotNull null },
                )
            },
    )
}

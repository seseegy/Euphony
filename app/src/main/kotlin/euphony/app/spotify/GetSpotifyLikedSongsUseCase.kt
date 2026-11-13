package euphony.app.spotify

import com.google.common.collect.ImmutableList
import euphony.app.spotify.models.SpotifyImage
import euphony.app.spotify.models.SpotifySimpleAlbum
import euphony.app.spotify.models.SpotifySimpleArtist
import euphony.app.spotify.models.SpotifyTrack
import javax.inject.Inject

class GetSpotifyLikedSongsUseCase
    @Inject
    constructor(
        private val repository: SpotifyLibraryRepository,
    ) {
        suspend operator fun invoke(): List<SpotifyLikedSong> =
            repository
                .likedSongs()
                .map(SpotifyTrack::toSpotifyLikedSong)
    }

data class SpotifyLikedSong(
    val id: String,
    val title: String,
    val artistNames: ImmutableList<String>,
    val thumbnailUrl: String?,
    val durationMs: Int,
    val isExplicit: Boolean,
) {
    fun toPlaybackTrack(): SpotifyTrack =
        SpotifyTrack(
            id = id,
            name = title,
            artists = artistNames.map { artistName -> SpotifySimpleArtist(name = artistName) },
            album =
                thumbnailUrl?.let { url ->
                    SpotifySimpleAlbum(images = listOf(SpotifyImage(url = url)))
                },
            durationMs = durationMs,
            explicit = isExplicit,
        )
}

private fun SpotifyTrack.toSpotifyLikedSong(): SpotifyLikedSong =
    SpotifyLikedSong(
        id = id,
        title = name,
        artistNames = ImmutableList.copyOf(artists.map { artist -> artist.name }),
        thumbnailUrl = SpotifyMapper.getTrackThumbnail(this),
        durationMs = durationMs,
        isExplicit = explicit,
    )

package euphony.app.domain.player

import androidx.media3.common.Player
import kotlinx.coroutines.flow.Flow
import euphony.app.canvas.CanvasPlaybackRequest
import euphony.app.canvas.CanvasPlaybackUseCase
import euphony.app.canvas.CanvasPolicy
import euphony.app.canvas.CanvasVideo
import euphony.app.db.entities.containerLabel
import euphony.app.db.entities.formattedBitrate
import euphony.app.db.entities.formattedSampleRate
import euphony.app.playback.ImmersivePlayerData
import euphony.app.playback.ImmersivePlayerRepository
import euphony.app.ui.player.immersive.ImmersivePlayerArtist
import euphony.app.ui.player.immersive.ImmersivePlayerUiModel
import java.util.Locale
import javax.inject.Inject

class ObserveImmersivePlayerUseCase @Inject constructor(
    private val repository: ImmersivePlayerRepository,
    private val canvasPlaybackUseCase: CanvasPlaybackUseCase,
) {
    val canvasPolicy = canvasPlaybackUseCase.policy
    val canvasRevision = canvasPlaybackUseCase.revision
    val spotifyConnected = canvasPlaybackUseCase.spotifyConnected

    operator fun invoke(): Flow<ImmersivePlayerData?> = repository.observePlayer()

    fun map(data: ImmersivePlayerData, canvas: CanvasVideo? = null): ImmersivePlayerUiModel {
        val metadata = data.metadata
        val formatDetails =
            data.format?.let { format ->
                listOfNotNull(
                    format.containerLabel().takeIf(String::isNotBlank),
                    format.formattedSampleRate(),
                    format.formattedBitrate(),
                ).joinToString(separator = " · ")
            }.orEmpty()
        return ImmersivePlayerUiModel(
            mediaId = metadata.id,
            title = metadata.title,
            artists = metadata.artists.map { ImmersivePlayerArtist(it.id, it.name) },
            albumId = metadata.album?.id,
            sourceTitle = metadata.album?.title?.takeIf(String::isNotBlank) ?: data.queueTitle.orEmpty(),
            artworkUrl = metadata.thumbnailUrl?.takeIf(String::isNotBlank)
                ?: data.song?.takeIf { it.id == metadata.id }?.thumbnailUrl?.takeIf(String::isNotBlank),
            isPlaying = data.isPlaying,
            isBuffering = data.playbackState == Player.STATE_BUFFERING,
            canSkipPrevious = data.canSkipPrevious,
            canSkipNext = data.canSkipNext,
            isLiked = data.song?.song?.liked == true,
            formatDetails = formatDetails,
            outputDevice = data.outputDevice,
            canvas = canvas,
            durationMs = metadata.duration.coerceAtLeast(0) * 1_000L,
        )
    }

    fun requestFor(data: ImmersivePlayerData): CanvasPlaybackRequest {
        val country = Locale.getDefault().country
        return CanvasPlaybackRequest(
            mediaId = data.metadata.id,
            title = data.metadata.title,
            artist = data.metadata.artists.firstOrNull()?.name.orEmpty(),
            storefront = if (country.length == 2) country.lowercase(Locale.ROOT) else "us",
            requireVertical = true,
        )
    }

    suspend fun loadCanvas(request: CanvasPlaybackRequest, policy: CanvasPolicy): CanvasVideo? =
        canvasPlaybackUseCase.load(request, policy)
}

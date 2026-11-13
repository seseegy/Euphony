package euphony.app.cast

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.StateFlow

fun interface CastMediaItemResolver {
    fun resolveForCast(mediaItem: MediaItem): MediaItem

    fun resolveForCastBlocking(mediaItem: MediaItem): MediaItem = resolveForCast(mediaItem)

    fun mimeTypeForCast(mediaItem: MediaItem): String? = null
}

interface CastPlaybackRepository {
    val screenState: StateFlow<CastScreenState>

    fun createPlayer(
        context: Context,
        localPlayer: ExoPlayer,
        mediaItemResolver: CastMediaItemResolver,
    ): Player

    fun disconnect()

    fun setVolume(volume: Float)
}

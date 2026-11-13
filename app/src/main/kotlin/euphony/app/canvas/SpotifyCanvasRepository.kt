package euphony.app.canvas

import kotlinx.coroutines.flow.first
import euphony.app.canvas.models.CanvasArtwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyCanvasRepository @Inject constructor(
    private val sessions: SpotifyCanvasSessionRepository,
) {
    val connected = sessions.connected

    suspend fun getBySongArtist(song: String, artist: String): CanvasArtwork? {
        if (!connected.first()) return null
        return sessions.withSession { session ->
            SpotifyCanvasProvider.getBySongArtist(song, artist, session.accessToken, session.clientId)
        }
    }

    suspend fun isHealthy(): Boolean = sessions.withSession { session ->
        SpotifyCanvasProvider.isHealthy(session.accessToken, session.clientId)
    }
}

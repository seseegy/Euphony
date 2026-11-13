package euphony.app.lyrics

import android.content.Context
import euphony.app.constants.EnableSimpMusicLyricsKey
import euphony.app.simpmusic.SimpMusicLyrics
import euphony.app.utils.dataStore
import euphony.app.utils.get

object SimpMusicLyricsProvider : LyricsProvider {
    override val name: String = "SimpMusic"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableSimpMusicLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> = SimpMusicLyrics.getLyrics(videoId = id, duration = duration)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        SimpMusicLyrics.getAllLyrics(videoId = id, duration = duration, callback = callback)
    }
}

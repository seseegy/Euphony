package euphony.app.lyrics

import android.content.Context
import euphony.app.constants.EnablePaxsenixAppleMusicLyricsKey
import euphony.app.paxsenix.PaxsenixLyrics
import euphony.app.utils.dataStore
import euphony.app.utils.get

object PaxsenixAppleMusicLyricsProvider : LyricsProvider {
    override val name = "Paxsenix: Apple Music"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnablePaxsenixAppleMusicLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> = PaxsenixLyrics.getAppleMusicLyrics(title, artist, duration)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        getLyrics(id, title, artist, album, duration).onSuccess(callback)
    }
}

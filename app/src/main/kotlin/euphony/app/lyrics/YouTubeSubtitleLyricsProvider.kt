package euphony.app.lyrics

import android.content.Context
import euphony.app.innertube.YouTube
import euphony.app.utils.YTPlayerUtils

object YouTubeSubtitleLyricsProvider : LyricsProvider {
    override val name = "YouTube Subtitle"

    override fun isEnabled(context: Context) = true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
    ): Result<String> {
        val authState = YTPlayerUtils.ensureWebPoTokensForSubtitles(id)
        return YouTube.transcript(id, authState)
    }

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        val authState = YTPlayerUtils.ensureWebPoTokensForSubtitles(id)
        YouTube.transcript(id, authState).onSuccess(callback)
    }
}

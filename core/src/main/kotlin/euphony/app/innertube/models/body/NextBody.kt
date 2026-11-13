package euphony.app.innertube.models.body

import kotlinx.serialization.Serializable
import euphony.app.innertube.models.Context

@Serializable
data class NextBody(
    val context: Context,
    val videoId: String?,
    val playlistId: String?,
    val playlistSetVideoId: String?,
    val index: Int?,
    val params: String?,
    val continuation: String?,
    val isAudioOnly: Boolean = true,
    val enablePersistentPlaylistPanel: Boolean = true,
)

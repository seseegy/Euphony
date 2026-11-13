package euphony.app.playback.stream

import euphony.app.constants.AudioQuality
import euphony.app.innertube.PlaybackAuthState

enum class StreamPurpose {
    PLAYBACK,
    DOWNLOAD,
}

enum class StreamSource {
    YOUTUBEI,
    MODULE,
}

data class AudioStreamRequest(
    val mediaId: String,
    val playlistId: String? = null,
    val quality: AudioQuality,
    val networkMetered: Boolean,
    val purpose: StreamPurpose,
    val authState: PlaybackAuthState,
    val pinnedFormatId: Int? = null,
    val requiresSongMetadata: Boolean = false,
    val moduleMetadata: euphony.app.modules.ModuleMatchMetadata? = null,
    val allowModules: Boolean = true,
)

data class ResolvedAudioStream(
    val url: String,
    val requestHeaders: Map<String, String>,
    val formatId: Int,
    val mimeType: String,
    val codecs: String,
    val bitrate: Int,
    val sampleRate: Int?,
    val contentLength: Long,
    val expiresAtMs: Long,
    val authFingerprint: String,
    val source: StreamSource,
    val runtimeVersion: String? = null,
    val title: String? = null,
    val durationSeconds: Int? = null,
    val thumbnailUrl: String? = null,
    val loudnessDb: Double? = null,
    val perceptualLoudnessDb: Double? = null,
    val playbackTrackingUrl: String? = null,
    val moduleSelection: euphony.app.modules.ModuleAudioSelection? = null,
)

fun euphony.app.modules.ModuleAudioSelection.toResolvedAudioStream() = ResolvedAudioStream(
    url = stream.url,
    requestHeaders = stream.headers,
    formatId = -1,
    mimeType = stream.mimeType,
    codecs = stream.codecs,
    bitrate = stream.bitrate,
    sampleRate = stream.sampleRate,
    contentLength = stream.contentLength,
    expiresAtMs = stream.expiresAtMs,
    authFingerprint = module.revision,
    source = StreamSource.MODULE,
    runtimeVersion = module.manifest.version,
    moduleSelection = this,
)

package euphony.app.playback.preload

import euphony.app.constants.AudioQuality
import euphony.app.innertube.PlaybackAuthState

data class PlaybackPerformanceSettings(
    val lowDataModeEnabled: Boolean,
    val preloadNextSongEnabled: Boolean,
    val hasPersistedValue: Boolean,
)

data class PlaybackPreloadConfiguration(
    val enabled: Boolean,
    val quality: AudioQuality,
    val authState: PlaybackAuthState,
)

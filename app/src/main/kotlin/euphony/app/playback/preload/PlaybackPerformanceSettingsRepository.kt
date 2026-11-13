package euphony.app.playback.preload

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import euphony.app.constants.AudioQuality
import euphony.app.constants.AudioQualityKey
import euphony.app.constants.LowDataModeKey
import euphony.app.constants.PreloadNextSongKey
import euphony.app.extensions.toEnum
import euphony.app.innertube.PlaybackAuthState
import euphony.app.utils.dataStore
import euphony.app.utils.toPlaybackAuthState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackPerformanceSettingsRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        val settings: Flow<PlaybackPerformanceSettings> =
            context.dataStore.data
                .map(::settingsFromPreferences)
                .distinctUntilChanged()

        val audioQuality: Flow<AudioQuality> =
            context.dataStore.data
                .map { preferences -> preferences[AudioQualityKey].toEnum(AudioQuality.AUTO) }
                .distinctUntilChanged()

        val playbackAuthState: Flow<PlaybackAuthState> =
            context.dataStore.data
                .map(Preferences::toPlaybackAuthState)
                .distinctUntilChanged()

        suspend fun updateSettings(
            transform: (PlaybackPerformanceSettings) -> PlaybackPerformanceSettings,
        ) {
            context.dataStore.edit { preferences ->
                val updatedSettings = transform(settingsFromPreferences(preferences))
                preferences[LowDataModeKey] = updatedSettings.lowDataModeEnabled
                preferences[PreloadNextSongKey] = updatedSettings.preloadNextSongEnabled
            }
        }

        private fun settingsFromPreferences(preferences: Preferences): PlaybackPerformanceSettings =
            PlaybackPerformanceSettings(
                lowDataModeEnabled = preferences[LowDataModeKey] ?: false,
                preloadNextSongEnabled = preferences[PreloadNextSongKey] ?: false,
                hasPersistedValue =
                    preferences.asMap().containsKey(LowDataModeKey) ||
                        preferences.asMap().containsKey(PreloadNextSongKey),
            )
    }

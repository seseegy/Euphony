package euphony.app.lyrics

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import euphony.app.constants.LyricsClickKey
import euphony.app.constants.LyricsLineBlurKey
import euphony.app.constants.LyricsLineSpacingKey
import euphony.app.constants.LyricsRomanizeChineseKey
import euphony.app.constants.LyricsRomanizeHindiKey
import euphony.app.constants.LyricsRomanizeJapaneseKey
import euphony.app.constants.LyricsRomanizeKoreanKey
import euphony.app.constants.LyricsRomanizeOtherLanguagesKey
import euphony.app.constants.LyricsScrollKey
import euphony.app.constants.LyricsTextSizeKey
import euphony.app.constants.LyricsV2BounceFactorKey
import euphony.app.constants.LyricsV2FillTransitionWidthKey
import euphony.app.constants.LyricsV2GlowFactorKey
import euphony.app.constants.LyricsV2LrcBounceEnabledKey
import euphony.app.constants.TranslateLyricsKey
import euphony.app.db.MusicDatabase
import euphony.app.utils.dataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRenderingRepository
    @Inject
    constructor(
        private val database: MusicDatabase,
        @ApplicationContext context: Context,
    ) {
        private val dataStore = context.dataStore
        private val preferences = dataStore.data

        suspend fun setShowTranslations(show: Boolean) {
            dataStore.edit { values -> values[TranslateLyricsKey] = show }
        }

        fun observeLyrics(mediaId: String): Flow<String?> =
            database
                .lyrics(mediaId)
                .map { entity -> entity?.lyrics }
                .distinctUntilChanged()

        fun observePreferences(): Flow<LyricsRenderingPreferences> =
            preferences
                .map { values ->
                    LyricsRenderingPreferences(
                        clickEnabled = values[LyricsClickKey] ?: true,
                        showTranslations = values[TranslateLyricsKey] ?: true,
                        scrollEnabled = values[LyricsScrollKey] ?: true,
                        textSizeSp = values[LyricsTextSizeKey] ?: 26f,
                        lineSpacing = values[LyricsLineSpacingKey] ?: 1.3f,
                        lineBlurEnabled = values[LyricsLineBlurKey] ?: true,
                        v2BounceFactor = values[LyricsV2BounceFactorKey] ?: 1f,
                        v2GlowFactor = values[LyricsV2GlowFactorKey] ?: 1f,
                        v2FillTransitionWidthDp = values[LyricsV2FillTransitionWidthKey] ?: 8f,
                        v2LrcBounceEnabled = values[LyricsV2LrcBounceEnabledKey] ?: true,
                        romanization =
                            LyricsRomanizationPreferences(
                                romanizeJapanese = values[LyricsRomanizeJapaneseKey] ?: true,
                                romanizeKorean = values[LyricsRomanizeKoreanKey] ?: true,
                                romanizeChinese = values[LyricsRomanizeChineseKey] ?: true,
                                romanizeHindi = values[LyricsRomanizeHindiKey] ?: true,
                                romanizeOther = values[LyricsRomanizeOtherLanguagesKey] ?: true,
                            ),
                    )
                }.distinctUntilChanged()
    }

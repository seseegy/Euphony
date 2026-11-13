package euphony.app.lyrics.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import euphony.app.db.MusicDatabase
import euphony.app.db.entities.LyricsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslatedLyricsRepository
    @Inject
    constructor(
        private val database: MusicDatabase,
    ) {
        suspend fun replaceLyrics(
            mediaId: String,
            lyrics: String,
        ) {
            withContext(Dispatchers.IO) {
                database.withTransaction {
                    replaceLyrics(
                        id = mediaId,
                        lyrics = lyrics,
                        source = LyricsEntity.Source.TRANSLATION.value,
                    )
                }
            }
        }
    }

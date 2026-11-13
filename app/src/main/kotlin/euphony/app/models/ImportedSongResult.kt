package euphony.app.models

import euphony.app.db.entities.Song

enum class ImportSource {
    LOCAL,
    YOUTUBE,
    UNRESOLVED,
}

data class ImportedSongResult(
    val originalSong: Song,
    val resolvedId: String?,
    val resolvedSong: Song?,
    val source: ImportSource,
) {
    val isResolved: Boolean
        get() = source != ImportSource.UNRESOLVED && resolvedId != null && resolvedSong != null
}

package euphony.app.library

import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableList
import euphony.app.models.MediaMetadata

@Immutable
data class LibraryTopMix(
    val id: String,
    val title: String,
    val description: String,
    val tracks: ImmutableList<MediaMetadata>,
)

@Immutable
data class GeneratedLibraryTopMix(
    val id: String,
    val title: String,
    val description: String,
    val tracks: ImmutableList<MediaMetadata>,
)

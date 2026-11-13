package euphony.app.canvas

import kotlinx.serialization.Serializable

@Serializable
enum class CanvasSource {
    BETTER_LYRICS,
    APPLE_MUSIC,
    TIDAL,
    SPOTIFY,
    ALL;

    fun accepts(provider: CanvasSource?): Boolean =
        provider != null && provider != ALL && (this == ALL || this == provider)

    companion object {
        fun fromPreference(value: String?): CanvasSource =
            if (value == "BOTH") ALL else entries.firstOrNull { it.name == value } ?: ALL
    }
}

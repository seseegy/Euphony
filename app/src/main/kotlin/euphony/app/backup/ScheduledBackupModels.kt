package euphony.app.backup

import android.net.Uri

enum class ScheduledBackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM,
}

data class ScheduledBackupSettings(
    val enabled: Boolean = false,
    val frequency: ScheduledBackupFrequency = ScheduledBackupFrequency.WEEKLY,
    val customDateEpochDay: Long? = null,
    val directoryUri: Uri? = null,
    val directoryName: String? = null,
    val overwriteExisting: Boolean = false,
)

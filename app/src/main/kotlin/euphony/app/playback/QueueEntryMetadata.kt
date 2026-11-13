package euphony.app.playback

import android.os.Bundle
import androidx.media3.common.MediaItem
import java.util.UUID

private const val ENTRY_ID = "euphony.queue.entry"
private const val RECOMMENDATION = "euphony.queue.recommendation"

internal val MediaItem.queueEntryId: String?
    get() = mediaMetadata.extras?.getString(ENTRY_ID)

internal val MediaItem.isQueueRecommendation: Boolean
    get() = mediaMetadata.extras?.getBoolean(RECOMMENDATION, false) == true

internal fun MediaItem.withQueueEntry(
    recommended: Boolean = isQueueRecommendation,
    id: String = queueEntryId ?: UUID.randomUUID().toString(),
): MediaItem = buildUpon().setMediaMetadata(
    mediaMetadata.buildUpon().setExtras(
        Bundle(mediaMetadata.extras ?: Bundle()).apply {
            putString(ENTRY_ID, id)
            putBoolean(RECOMMENDATION, recommended)
        },
    ).build(),
).build()


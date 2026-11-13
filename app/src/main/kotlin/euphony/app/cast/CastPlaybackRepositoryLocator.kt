package euphony.app.cast

import android.content.Context

object CastPlaybackRepositoryLocator {
    @Volatile private var repository: CastPlaybackRepository? = null

    fun get(context: Context): CastPlaybackRepository =
        repository ?: synchronized(this) {
            repository ?: DefaultCastPlaybackRepository(context.applicationContext).also { repository = it }
        }
}

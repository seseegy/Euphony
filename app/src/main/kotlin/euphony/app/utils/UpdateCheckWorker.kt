package euphony.app.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import euphony.app.BuildConfig
import euphony.app.constants.AutomaticUpdateCheckKey

class UpdateCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (!BuildConfig.UPDATER_AVAILABLE) {
            return Result.success()
        }

        return try {
            val dataStore = applicationContext.dataStore

            val preferences = dataStore.data.first()
            val automaticChecksEnabled = preferences[AutomaticUpdateCheckKey] ?: true
            if (!automaticChecksEnabled) return Result.success()

            val latestVersion = Updater.getLatestVersionName().getOrElse { throw it }

            if (Updater.isUpdateAvailable(latestVersion, BuildConfig.VERSION_NAME)) {
                UpdateNotificationManager.notifyIfNewVersion(
                    applicationContext,
                    latestVersion,
                )
            }

            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            reportException(exception)
            Result.retry()
        }
    }
}

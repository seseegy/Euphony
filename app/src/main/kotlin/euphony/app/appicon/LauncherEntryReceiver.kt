package euphony.app.appicon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import euphony.app.utils.reportException

class LauncherEntryReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val restoreLauncherEntry =
            EntryPointAccessors
                .fromApplication(context.applicationContext, LauncherEntryReceiverEntryPoint::class.java)
                .restoreLauncherEntry()
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                restoreLauncherEntry()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                reportException(error)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LauncherEntryReceiverEntryPoint {
    fun restoreLauncherEntry(): RestoreLauncherEntryUseCase
}

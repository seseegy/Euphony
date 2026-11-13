package euphony.app.widget

import android.content.ComponentName
import android.content.Context
import euphony.app.playback.MusicService

internal suspend fun requestPlaybackWidgetUpdate(context: Context) {
    val serviceIntent = android.content.Intent(context, MusicService::class.java)
    val connection =
        object : android.content.ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                binder: android.os.IBinder?,
            ) {
                val service = (binder as? MusicService.MusicBinder)?.service
                service?.updateWidget()
                runCatching { context.unbindService(this) }
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }

    runCatching {
        context.bindService(serviceIntent, connection, 0)
    }
}

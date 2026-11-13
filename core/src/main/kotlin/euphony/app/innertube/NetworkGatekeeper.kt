package euphony.app.innertube

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

object NetworkGatekeeper : Interceptor {
    private val connectionBlocked = AtomicBoolean(true)

    val isConnectionBlocked: Boolean
        get() = connectionBlocked.get()

    fun setConnectionBlocked(blocked: Boolean) {
        connectionBlocked.set(blocked)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (connectionBlocked.get()) {
            throw IOException(
                "Connection blocked. This is a private Euphony build.",
            )
        }
        return chain.proceed(chain.request())
    }
}

object NetworkGatekeeperControl {
    fun allowConnections() {
        NetworkGatekeeper.setConnectionBlocked(false)
    }
}

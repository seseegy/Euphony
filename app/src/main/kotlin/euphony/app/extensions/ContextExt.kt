package euphony.app.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import euphony.app.constants.InnerTubeCookieKey
import euphony.app.constants.YtmSyncKey
import euphony.app.innertube.utils.hasYouTubeLoginCookie
import euphony.app.utils.dataStore
import euphony.app.utils.get

fun Context.isSyncEnabled(): Boolean = dataStore.get(YtmSyncKey, true) && isUserLoggedIn()

fun Context.isUserLoggedIn(): Boolean {
    val cookie = dataStore[InnerTubeCookieKey] ?: ""
    return hasYouTubeLoginCookie(cookie) && isInternetConnected()
}

fun Context.isInternetConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}

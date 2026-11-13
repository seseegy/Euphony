package euphony.app.ui.utils

import android.net.Uri
import androidx.navigation.NavController
import euphony.app.innertube.models.PODCAST_SHOW_BROWSE_PREFIX
import euphony.app.ui.screens.Screens

internal fun NavController.navigateToArtist(artistId: String?) {
    navigateToMediaDetail(artistId, "artist")
}

internal fun NavController.navigateToAlbum(albumId: String?) {
    navigateToMediaDetail(albumId, "album")
}

private fun NavController.navigateToMediaDetail(id: String?, destination: String) {
    if (id.isNullOrBlank()) return
    val route = if (id.startsWith(PODCAST_SHOW_BROWSE_PREFIX)) "podcast" else destination
    navigate("$route/${Uri.encode(id)}")
}

fun NavController.backToMain() {
    val mainRoutes = Screens.MainScreens.map { it.route }

    while (previousBackStackEntry != null &&
        currentBackStackEntry?.destination?.route !in mainRoutes
    ) {
        popBackStack()
    }
}

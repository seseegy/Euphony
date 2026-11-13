package euphony.app.automotive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import euphony.app.ui.screens.settings.AndroidAutoSettingsRoute
import euphony.app.ui.theme.EuphonyTheme

@AndroidEntryPoint
class AutomotiveSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EuphonyTheme(disableAnimations = true) {
                AndroidAutoSettingsRoute(onBack = ::finish)
            }
        }
    }
}

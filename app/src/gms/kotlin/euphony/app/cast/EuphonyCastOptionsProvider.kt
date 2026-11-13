package euphony.app.cast

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import euphony.app.R

class EuphonyCastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions {
        val receiverApplicationId =
            context
                .getString(R.string.cast_receiver_application_id)
                .takeIf(String::isNotBlank)
                ?: CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID

        return CastOptions
            .Builder()
            .setReceiverApplicationId(receiverApplicationId)
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}

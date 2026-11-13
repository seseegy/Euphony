package euphony.app

import android.app.Application
import android.content.Context

object LeakCanaryController {
    private const val VARIANT_CLASS = "euphony.app.LeakCanaryVariant"

    fun initialize(application: Application) {
        invoke(application, "initialize", application)
    }

    fun setEnabled(
        context: Context,
        enabled: Boolean,
    ) {
        invoke(context, "setEnabled", context, enabled)
    }

    private fun invoke(
        context: Context,
        methodName: String,
        vararg arguments: Any,
    ) {
        runCatching {
            val variantClass = Class.forName(VARIANT_CLASS)
            val parameterTypes =
                when (methodName) {
                    "initialize" -> arrayOf(Application::class.java)
                    "setEnabled" -> arrayOf(Context::class.java, Boolean::class.javaPrimitiveType!!)
                    else -> return
                }
            variantClass.getMethod(methodName, *parameterTypes).invoke(null, *arguments)
        }.onFailure { throwable ->
            if (throwable !is ClassNotFoundException) {
                android.util.Log.w("LeakCanaryController", "Unable to invoke LeakCanary variant", throwable)
            }
        }
    }
}

internal object LeakCanaryToggle {
    const val PREFERENCE_KEY = "dev_enable_leak_canary"
}

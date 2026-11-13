package euphony.app.canvas

import okhttp3.Interceptor
import okhttp3.Response

object CanvasRequestPolicy {
    @Volatile
    var check: (CanvasSource) -> Unit = {}

    @Volatile
    var intercept: (Interceptor.Chain, CanvasSource) -> Response = { chain, _ -> chain.proceed(chain.request()) }
}

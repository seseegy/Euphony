package euphony.app.ai

import androidx.compose.runtime.Immutable
import euphony.app.constants.AiProvider

@Immutable
data class AiModelOption(
    val id: String,
    val displayName: String,
)

@Immutable
data class AiServiceConfig(
    val provider: AiProvider,
    val apiKey: String,
    val customEndpoint: String,
    val model: String,
    val requestParameters: String = "",
) {
    val canCallApi: Boolean
        get() =
            provider != AiProvider.NONE &&
                apiKey.isNotBlank() &&
                (provider != AiProvider.CUSTOM || customEndpoint.isNotBlank())
}

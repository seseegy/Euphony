package euphony.app.auth

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Immutable
data class MenuProfile(
    val name: String,
    val email: String,
)

class ObserveMenuProfileUseCase
    @Inject
    constructor(
        private val repository: YouTubeLoginRepository,
    ) {
        operator fun invoke(): Flow<MenuProfile?> =
            repository.observeAccountIdentity().map { identity ->
                if (identity.isLoggedIn) {
                    MenuProfile(name = identity.name.trim(), email = identity.email.trim())
                } else {
                    null
                }
            }
    }

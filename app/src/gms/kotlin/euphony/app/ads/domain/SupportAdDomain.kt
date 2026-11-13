package euphony.app.ads.domain

import javax.inject.Inject

internal enum class SupportPageOpenResult {
    Opened,
    Unavailable,
}

internal interface SupportPageRepository {
    fun openSupportPage(): SupportPageOpenResult
}

internal class OpenSupportPageUseCase
    @Inject
    constructor(
        private val repository: SupportPageRepository,
    ) {
        operator fun invoke(): SupportPageOpenResult = repository.openSupportPage()
    }

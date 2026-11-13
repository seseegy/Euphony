package euphony.app.gatekeeper

import javax.inject.Inject

class RunGatekeeperCheckUseCase
    @Inject
    constructor(
        private val repository: GatekeeperRepository,
    ) {
        suspend operator fun invoke(): GatekeeperResult = repository.checkAccess()
    }

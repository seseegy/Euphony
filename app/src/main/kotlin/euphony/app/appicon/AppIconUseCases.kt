package euphony.app.appicon

import javax.inject.Inject

class LoadAppIconsUseCase
    @Inject
    constructor(
        private val repository: AppIconRepository,
    ) {
        suspend operator fun invoke(): AppIconCatalog = repository.loadCatalog()
    }

class SelectAppIconUseCase
    @Inject
    constructor(
        private val repository: AppIconRepository,
    ) {
        suspend operator fun invoke(iconId: String): AppIconCatalog = repository.selectIcon(iconId)
    }

class RestoreLauncherEntryUseCase
    @Inject
    constructor(
        private val repository: AppIconRepository,
    ) {
        suspend operator fun invoke() = repository.restoreLauncherEntry()
    }

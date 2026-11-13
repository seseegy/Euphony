package euphony.app.library

import kotlinx.coroutines.flow.Flow
import euphony.app.repository.LibraryTopMixRepository
import javax.inject.Inject

class ObserveLibraryTopMixesUseCase
    @Inject
    constructor(
        private val repository: LibraryTopMixRepository,
    ) {
        operator fun invoke(): Flow<List<LibraryTopMix>> = repository.observePersistedTopMixes()
    }

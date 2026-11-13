package euphony.app.artist

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveArtistBlockedUseCase
    @Inject
    constructor(
        private val repository: ArtistBlockRepository,
    ) {
        operator fun invoke(artistId: String): Flow<Boolean?> = repository.observeBlocked(artistId)
    }

class SetArtistBlockedUseCase
    @Inject
    constructor(
        private val repository: ArtistBlockRepository,
    ) {
        suspend operator fun invoke(request: ArtistBlockRequest) {
            require(request.id.isNotBlank())
            require(request.name.isNotBlank())
            repository.setBlocked(request)
        }
    }

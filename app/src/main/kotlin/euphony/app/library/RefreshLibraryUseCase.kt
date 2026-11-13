package euphony.app.library

import euphony.app.utils.LibraryLoginRequiredException
import euphony.app.utils.LibrarySyncDisabledException
import euphony.app.utils.SyncUtils
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

enum class LibrarySyncTarget {
    All,
    LikedSongs,
    Songs,
    Artists,
    Albums,
    Playlists,
    Podcasts,
}

enum class LibrarySyncFailure {
    LoginRequired,
    SyncDisabled,
    RequestFailed,
}

sealed interface RefreshLibraryResult {
    data object Success : RefreshLibraryResult

    data class Failure(val reason: LibrarySyncFailure) : RefreshLibraryResult
}

class RefreshLibraryUseCase
    @Inject
    constructor(
        private val repository: SyncUtils,
    ) {
        suspend operator fun invoke(target: LibrarySyncTarget): RefreshLibraryResult =
            try {
                repository.requireLibrarySyncEnabled()
                when (target) {
                    LibrarySyncTarget.All -> repository.performFullSync(propagateFailures = true)
                    LibrarySyncTarget.LikedSongs -> repository.syncLikedSongs(propagateFailures = true)
                    LibrarySyncTarget.Songs -> repository.syncLibrarySongs(propagateFailures = true)
                    LibrarySyncTarget.Artists -> repository.syncArtistsSubscriptions(propagateFailures = true)
                    LibrarySyncTarget.Albums -> repository.syncLikedAlbums(propagateFailures = true)
                    LibrarySyncTarget.Playlists -> {
                        repository.syncSavedPlaylists(propagateFailures = true)
                        repository.syncAutoSyncPlaylists(propagateFailures = true)
                    }
                    LibrarySyncTarget.Podcasts -> repository.syncSavedPodcasts(propagateFailures = true)
                }
                RefreshLibraryResult.Success
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Timber.e(e, "Library refresh failed for %s", target)
                RefreshLibraryResult.Failure(
                    when (e) {
                        is LibraryLoginRequiredException -> LibrarySyncFailure.LoginRequired
                        is LibrarySyncDisabledException -> LibrarySyncFailure.SyncDisabled
                        else -> LibrarySyncFailure.RequestFailed
                    },
                )
            }
    }

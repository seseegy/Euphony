package euphony.app.updates

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import euphony.app.utils.ReleaseInfo
import euphony.app.utils.Updater
import javax.inject.Inject

class ChangelogRepository @Inject constructor() {
    fun observeReleases(): Flow<List<ReleaseInfo>> =
        flow {
            val cachedReleases = Updater.getCachedReleases()
            if (cachedReleases.isNotEmpty()) emit(cachedReleases)
            emit(Updater.getAllReleases(forceRefresh = true).getOrThrow())
        }.flowOn(Dispatchers.IO)
}

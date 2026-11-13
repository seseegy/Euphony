package euphony.app.ads.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import euphony.app.ads.domain.SupportPageOpenResult
import euphony.app.ads.domain.SupportPageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BrowserSupportPageRepository
    @Inject
    constructor() : SupportPageRepository {
        // Private Euphony build: no support page exists upstream.
        override fun openSupportPage(): SupportPageOpenResult =
            SupportPageOpenResult.Unavailable
    }

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SupportPageDataModule {
    @Binds
    @Singleton
    abstract fun bindSupportPageRepository(repository: BrowserSupportPageRepository): SupportPageRepository
}

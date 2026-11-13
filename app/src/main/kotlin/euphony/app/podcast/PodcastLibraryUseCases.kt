package euphony.app.podcast

import com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSavedPodcastsUseCase
    @Inject
    constructor(
        private val repository: PodcastRepository,
    ) {
        operator fun invoke(): Flow<PodcastLibraryUiState> =
            repository.observeSavedPodcasts().map { podcasts ->
                PodcastLibraryUiState(
                    podcasts =
                        ImmutableList.copyOf(
                            podcasts.map { podcast ->
                                PodcastLibraryItemUiModel(
                                    browseId = podcast.browseId,
                                    title = podcast.title,
                                    author = podcast.authorName,
                                    thumbnailUrl = podcast.thumbnailUrl,
                                    isSavedLocally = podcast.localSavedAt != null,
                                    isSavedRemotely = podcast.remoteSavedAt != null,
                                )
                            },
                        ),
                )
            }
    }

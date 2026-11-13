package euphony.app.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.common.collect.ImmutableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import euphony.app.R
import euphony.app.library.LibrarySyncTarget
import euphony.app.library.RefreshLibraryUseCase
import euphony.app.podcast.LoadPodcastUseCase
import euphony.app.podcast.ObserveSavedPodcastsUseCase
import euphony.app.podcast.PodcastLibraryAction
import euphony.app.podcast.PodcastLibraryEvent
import euphony.app.podcast.PodcastLibraryScreenState
import euphony.app.podcast.PodcastPlaybackRequest
import euphony.app.podcast.TogglePodcastSaveUseCase
import euphony.app.utils.LibraryLoginRequiredException
import euphony.app.utils.LibrarySyncDisabledException
import euphony.app.utils.reportException
import javax.inject.Inject

@HiltViewModel
class PodcastLibraryViewModel
    @Inject
    constructor(
        observeSavedPodcasts: ObserveSavedPodcastsUseCase,
        refreshLibrary: RefreshLibraryUseCase,
        private val loadPodcast: LoadPodcastUseCase,
        private val setPodcastSaved: TogglePodcastSaveUseCase,
    ) : LibraryRefreshViewModel(refreshLibrary) {
        private val _screenState = MutableStateFlow<PodcastLibraryScreenState>(PodcastLibraryScreenState.Loading)
        val screenState = _screenState.asStateFlow()
        private val eventChannel = Channel<PodcastLibraryEvent>(Channel.BUFFERED)
        val events = eventChannel.receiveAsFlow()
        private var playJob: Job? = null
        private val removeJobs = mutableMapOf<String, Job>()

        init {
            viewModelScope.launch {
                try {
                    observeSavedPodcasts().collect { uiState ->
                        _screenState.value =
                            if (uiState.podcasts.isEmpty()) {
                                PodcastLibraryScreenState.Empty
                            } else {
                                PodcastLibraryScreenState.Success(uiState)
                            }
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    reportException(error)
                    _screenState.value = PodcastLibraryScreenState.Error(R.string.error_unknown)
                }
            }
            sync()
        }

        fun onAction(action: PodcastLibraryAction) {
            when (action) {
                PodcastLibraryAction.Refresh -> sync()
                is PodcastLibraryAction.PlayPodcast -> playPodcast(action.browseId)
                is PodcastLibraryAction.RemovePodcast -> removePodcast(action.browseId)
            }
        }

        private fun sync() {
            refreshLibrary(LibrarySyncTarget.Podcasts)
        }

        private fun playPodcast(browseId: String) {
            playJob?.cancel()
            playJob =
                viewModelScope.launch {
                    loadPodcast(browseId)
                        .onSuccess { result ->
                            val episodes = result.uiState.episodes
                            if (episodes.isEmpty()) {
                                eventChannel.send(PodcastLibraryEvent.ShowMessage(R.string.podcast_has_no_episodes))
                            } else {
                                eventChannel.send(
                                    PodcastLibraryEvent.Play(
                                        PodcastPlaybackRequest(
                                            title = result.uiState.title,
                                            items = ImmutableList.copyOf(episodes.map { episode -> episode.playbackMetadata }),
                                            startIndex = 0,
                                        ),
                                    ),
                                )
                            }
                        }.onFailure { throwable ->
                            reportException(throwable)
                            eventChannel.send(PodcastLibraryEvent.ShowMessage(R.string.error_unknown))
                        }
                }
        }

        private fun removePodcast(browseId: String) {
            if (removeJobs[browseId]?.isActive == true) return
            removeJobs[browseId] =
                viewModelScope.launch {
                    try {
                        setPodcastSaved(browseId, false).onFailure { throwable ->
                            reportException(throwable)
                            eventChannel.send(PodcastLibraryEvent.ShowMessage(throwable.messageResId()))
                        }
                    } finally {
                        removeJobs.remove(browseId)
                    }
                }
        }

        private fun Throwable.messageResId(): Int =
            when (this) {
                is LibraryLoginRequiredException -> R.string.not_logged_in_youtube
                is LibrarySyncDisabledException -> R.string.sync_disabled
                else -> R.string.error_unknown
            }
    }

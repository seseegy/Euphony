package euphony.app.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import euphony.app.constants.HideVideoKey
import euphony.app.db.MusicDatabase
import euphony.app.extensions.filterBlockedArtists
import euphony.app.extensions.filterVideo
import euphony.app.innertube.YouTube
import euphony.app.innertube.models.AlbumItem
import euphony.app.utils.dataStore
import euphony.app.utils.reportException
import javax.inject.Inject

sealed interface AlbumUiState {
    data object Loading : AlbumUiState

    data object Success : AlbumUiState

    data object Empty : AlbumUiState

    data class Error(
        val isNotFound: Boolean = false,
    ) : AlbumUiState
}

private sealed interface FetchState {
    data object Pending : FetchState

    data object Success : FetchState

    data class Failed(
        val isNotFound: Boolean = false,
    ) : FetchState
}

@HiltViewModel
class AlbumViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val database: MusicDatabase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val albumId = savedStateHandle.get<String>("albumId")!!
        val playlistId = MutableStateFlow("")
        val albumWithSongs =
            combine(
                database.albumWithSongs(albumId),
                context.dataStore.data
                    .map { preferences -> preferences[HideVideoKey] ?: false }
                    .distinctUntilChanged(),
            ) { album, hideVideo ->
                album?.copy(
                    songs =
                        if (album.artists.any { it.blockedAt != null }) {
                            emptyList()
                        } else {
                            album.songs
                                .filterBlockedArtists()
                                .filterVideo(hideVideo)
                        },
                )
            }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
        var otherVersions = MutableStateFlow<List<AlbumItem>>(emptyList())

        private var fetchJob: Job? = null

        private val _fetchState = MutableStateFlow<FetchState>(FetchState.Pending)

        val uiState: StateFlow<AlbumUiState> =
            combine(albumWithSongs, _fetchState) { data, fetch ->
                when {
                    data != null && data.songs.isNotEmpty() -> AlbumUiState.Success
                    fetch is FetchState.Pending -> AlbumUiState.Loading
                    fetch is FetchState.Failed && data == null -> AlbumUiState.Error(fetch.isNotFound)
                    fetch is FetchState.Success -> AlbumUiState.Empty
                    fetch is FetchState.Failed && data != null -> AlbumUiState.Empty
                    else -> AlbumUiState.Loading
                }
            }.stateIn(viewModelScope, SharingStarted.Eagerly, AlbumUiState.Loading)

        init {
            retry()
        }

        fun retry() {
            if (fetchJob?.isActive == true) return
            fetchJob = viewModelScope.launch(Dispatchers.IO) {
                _fetchState.value = FetchState.Pending
                try {
                    val album = database.album(albumId).first()
                    if (album?.album?.isLocal == true) {
                        _fetchState.value = FetchState.Success
                        return@launch
                    }
                    val page = YouTube.album(album?.album?.id ?: albumId).getOrThrow()
                    playlistId.value = page.album.playlistId
                    val blockedArtistIds = database.getBlockedArtistIds().toSet()
                    otherVersions.value = page.otherVersions.filter { version ->
                        version.artists.orEmpty().none { artist -> artist.id in blockedArtistIds }
                    }
                    database.withTransaction {
                        if (album == null) {
                            insert(page)
                        } else {
                            update(album.album, page, album.artists)
                        }
                    }
                    _fetchState.value = FetchState.Success
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (throwable: Throwable) {
                    reportException(throwable)
                    _fetchState.value = FetchState.Failed(
                        isNotFound = throwable.message?.contains("NOT_FOUND") == true,
                    )
                }
            }
        }
    }

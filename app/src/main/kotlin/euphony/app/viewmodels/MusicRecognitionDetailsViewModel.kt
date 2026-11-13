package euphony.app.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import euphony.app.musicrecognition.DecodeRecognitionDetailsUseCase
import javax.inject.Inject

sealed interface MusicRecognitionDetailsState {
    data object Loading : MusicRecognitionDetailsState

    @Immutable
    data class Success(val track: RecognizedTrackUiModel) : MusicRecognitionDetailsState

    data object Empty : MusicRecognitionDetailsState

    data object Error : MusicRecognitionDetailsState
}

sealed interface MusicRecognitionDetailsEvent {
    data object ListenAgain : MusicRecognitionDetailsEvent

    data class Search(val query: String) : MusicRecognitionDetailsEvent

    data class OpenUri(val uri: String) : MusicRecognitionDetailsEvent
}

@HiltViewModel
class MusicRecognitionDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    decodeRecognitionDetails: DecodeRecognitionDetailsUseCase,
) : ViewModel() {
    private val _screenState =
        MutableStateFlow<MusicRecognitionDetailsState>(MusicRecognitionDetailsState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _events = Channel<MusicRecognitionDetailsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val result = decodeRecognitionDetails(savedStateHandle.get<String>("encodedTrack").orEmpty())
            _screenState.value = result.fold(
                onSuccess = { track ->
                    if (track == null) {
                        MusicRecognitionDetailsState.Empty
                    } else {
                        MusicRecognitionDetailsState.Success(track.toRecognitionUiModel())
                    }
                },
                onFailure = { MusicRecognitionDetailsState.Error },
            )
        }
    }

    fun onListenAgainRequested() {
        _events.trySend(MusicRecognitionDetailsEvent.ListenAgain)
    }

    fun onSearchRequested() {
        val state = _screenState.value as? MusicRecognitionDetailsState.Success ?: return
        _events.trySend(MusicRecognitionDetailsEvent.Search(state.track.searchQuery))
    }

    fun onShazamRequested() {
        val state = _screenState.value as? MusicRecognitionDetailsState.Success ?: return
        val uri = state.track.shazamUrl ?: return
        _events.trySend(MusicRecognitionDetailsEvent.OpenUri(uri))
    }
}

package euphony.app.ads.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import euphony.app.ads.domain.OpenSupportPageUseCase
import euphony.app.ads.domain.SupportPageOpenResult
import javax.inject.Inject

internal sealed interface SupportEuphonyScreenState {
    @Immutable
    data object Loading : SupportEuphonyScreenState

    @Immutable
    data object Success : SupportEuphonyScreenState

    @Immutable
    data object Empty : SupportEuphonyScreenState

    @Immutable
    data class Error(
        val reason: SupportEuphonyError,
    ) : SupportEuphonyScreenState
}

internal enum class SupportEuphonyError {
    PageUnavailable,
}

internal enum class SupportEuphonyUiEvent {
    OpenFailed,
}

@HiltViewModel
internal class SupportEuphonyViewModel
    @Inject
    constructor(
        private val openSupportPage: OpenSupportPageUseCase,
    ) : ViewModel() {
        private val _screenState =
            MutableStateFlow<SupportEuphonyScreenState>(SupportEuphonyScreenState.Success)
        val screenState: StateFlow<SupportEuphonyScreenState> = _screenState.asStateFlow()

        private val eventChannel = Channel<SupportEuphonyUiEvent>(Channel.BUFFERED)
        val events = eventChannel.receiveAsFlow()

        fun onSupportEuphonyClick() {
            if (_screenState.value is SupportEuphonyScreenState.Loading) return
            _screenState.value = SupportEuphonyScreenState.Loading
            when (openSupportPage()) {
                SupportPageOpenResult.Opened -> {
                    _screenState.value = SupportEuphonyScreenState.Success
                }

                SupportPageOpenResult.Unavailable -> {
                    _screenState.value =
                        SupportEuphonyScreenState.Error(SupportEuphonyError.PageUnavailable)
                    eventChannel.trySend(SupportEuphonyUiEvent.OpenFailed)
                }
            }
        }
    }

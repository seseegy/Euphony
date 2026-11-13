package euphony.app.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class GlassStyleTarget {
    MINI_PLAYER,
    NAVIGATION_BAR,
}

@HiltViewModel
class GlassStyleConfirmationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val pendingTarget: StateFlow<GlassStyleTarget?> = savedStateHandle.getStateFlow(PendingTargetKey, null)

    fun requestConfirmation(target: GlassStyleTarget) {
        savedStateHandle[PendingTargetKey] = target
    }

    fun cancelConfirmation() {
        savedStateHandle[PendingTargetKey] = null
    }

    fun confirmSelection(): GlassStyleTarget? {
        val target = pendingTarget.value ?: return null
        cancelConfirmation()
        return target
    }

    private companion object {
        const val PendingTargetKey = "pendingGlassStyleTarget"
    }
}

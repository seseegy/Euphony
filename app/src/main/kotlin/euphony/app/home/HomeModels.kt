package euphony.app.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableList
import euphony.app.constants.QuickPicks
import euphony.app.constants.QuickPicksDisplayMode
import euphony.app.db.entities.LocalItem
import euphony.app.db.entities.Song
import euphony.app.innertube.models.PlaylistItem
import euphony.app.innertube.pages.HomePage
import euphony.app.models.SimilarRecommendation
import euphony.app.podcast.PodcastPlaybackRequest

sealed interface HomeScreenState {
    data object Loading : HomeScreenState

    @Immutable
    data class Success(
        val uiState: HomeUiState,
    ) : HomeScreenState

    data object Empty : HomeScreenState

    @Immutable
    data class Error(
        @StringRes val messageResId: Int,
    ) : HomeScreenState
}

@Immutable
data class HomeUiState(
    val quickPicks: ImmutableList<Song>,
    val speedDialItems: ImmutableList<LocalItem>,
    val forgottenFavorites: ImmutableList<Song>,
    val keepListening: ImmutableList<LocalItem>,
    val similarRecommendations: ImmutableList<SimilarRecommendation>,
    val accountPlaylists: ImmutableList<PlaylistItem>,
    val homePage: HomePage?,
    val remoteQuickPicks: HomePage.Section?,
    val communitySection: HomePage.Section?,
    val selectedChip: HomePage.Chip?,
    val accountName: String,
    val accountImageUrl: String?,
    val quickPicksMode: QuickPicks,
    val quickPicksDisplayMode: QuickPicksDisplayMode,
    val showCategoryChips: Boolean,
    val showTonalBackdrop: Boolean,
    val isRefreshing: Boolean,
    val isLoadingMore: Boolean,
)

fun HomePage.Section.hasVisibleContent(): Boolean = items.isNotEmpty() || featuredCards.isNotEmpty()

sealed interface HomeAction {
    data object Refresh : HomeAction

    data class SelectChip(
        val chip: HomePage.Chip?,
    ) : HomeAction

    data class LoadMore(
        val continuation: String?,
    ) : HomeAction

    data class OpenRemoteItem(
        val itemId: String,
    ) : HomeAction
}

sealed interface HomeEvent {
    data class OpenPodcast(
        val browseId: String,
    ) : HomeEvent

    @Immutable
    data class PlayPodcastEpisode(
        val request: PodcastPlaybackRequest,
    ) : HomeEvent
}

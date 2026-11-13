package euphony.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import euphony.app.innertube.YouTube
import euphony.app.innertube.models.AlbumItem
import euphony.app.innertube.models.ArtistItem
import euphony.app.innertube.models.PlaylistItem
import euphony.app.innertube.utils.completed
import euphony.app.utils.reportException
import javax.inject.Inject

enum class AccountContentType {
    PLAYLISTS,
    ALBUMS,
    ARTISTS,
}

@HiltViewModel
class AccountViewModel
    @Inject
    constructor() : ViewModel() {
        val playlists = MutableStateFlow<List<PlaylistItem>?>(null)
        val albums = MutableStateFlow<List<AlbumItem>?>(null)
        val artists = MutableStateFlow<List<ArtistItem>?>(null)

        // Selected content type for chips
        val selectedContentType = MutableStateFlow(AccountContentType.PLAYLISTS)

        init {
            viewModelScope.launch {
                YouTube
                    .library("FEmusic_liked_playlists")
                    .completed()
                    .onSuccess {
                        playlists.value =
                            it.items
                                .filterIsInstance<PlaylistItem>()
                                .filterNot { it.id == "SE" }
                    }.onFailure {
                        reportException(it)
                    }
                YouTube
                    .library("FEmusic_liked_albums")
                    .completed()
                    .onSuccess {
                        albums.value = it.items.filterIsInstance<AlbumItem>()
                    }.onFailure {
                        reportException(it)
                    }
                YouTube
                    .library("FEmusic_library_corpus_artists")
                    .completed()
                    .onSuccess {
                        artists.value = it.items.filterIsInstance<ArtistItem>()
                    }.onFailure {
                        reportException(it)
                    }
            }
        }

        fun setSelectedContentType(contentType: AccountContentType) {
            selectedContentType.value = contentType
        }
    }

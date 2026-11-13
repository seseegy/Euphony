package euphony.app.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import euphony.app.constants.AutoPlaylistSongSortDescendingKey
import euphony.app.constants.AutoPlaylistSongSortType
import euphony.app.constants.AutoPlaylistSongSortTypeKey
import euphony.app.constants.HideExplicitKey
import euphony.app.constants.HideVideoKey
import euphony.app.constants.SongSortType
import euphony.app.db.MusicDatabase
import euphony.app.extensions.filterExplicit
import euphony.app.extensions.toEnum
import euphony.app.utils.SyncUtils
import euphony.app.utils.dataStore
import euphony.app.utils.get
import euphony.app.utils.reportException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AutoPlaylistViewModel
    @Inject
    constructor(
        @ApplicationContext context: Context,
        database: MusicDatabase,
        savedStateHandle: SavedStateHandle,
        private val syncUtils: SyncUtils,
    ) : ViewModel() {
        val playlist = savedStateHandle.get<String>("playlist")!!

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing = _isRefreshing.asStateFlow()

        private fun AutoPlaylistSongSortType.toSongSortType(): SongSortType =
            when (this) {
                AutoPlaylistSongSortType.CREATE_DATE -> SongSortType.CREATE_DATE
                AutoPlaylistSongSortType.NAME -> SongSortType.NAME
                AutoPlaylistSongSortType.ARTIST -> SongSortType.ARTIST
                AutoPlaylistSongSortType.PLAY_TIME -> SongSortType.PLAY_TIME
            }

        @OptIn(ExperimentalCoroutinesApi::class)
        val likedSongs =
            context.dataStore.data
                .map {
                    Triple(
                        it[AutoPlaylistSongSortTypeKey].toEnum(AutoPlaylistSongSortType.CREATE_DATE) to (
                            it[AutoPlaylistSongSortDescendingKey]
                                ?: true
                        ),
                        it[HideExplicitKey] ?: false,
                        it[HideVideoKey] ?: false,
                    )
                }.distinctUntilChanged()
                .flatMapLatest { (sortDesc, hideExplicit, hideVideo) ->
                    val (sortType, descending) = sortDesc
                    val songSortType = sortType.toSongSortType()
                    when (playlist) {
                        "liked" -> {
                            database.likedSongs(songSortType, descending, hideVideo).map { it.filterExplicit(hideExplicit) }
                        }

                        else -> {
                            MutableStateFlow(emptyList())
                        }
                    }
                }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        fun refresh() {
            if (_isRefreshing.value) return
            viewModelScope.launch(Dispatchers.IO) {
                _isRefreshing.value = true
                try {
                    when (playlist) {
                        "liked" -> syncUtils.syncLikedSongs()
                        else -> Unit
                    }
                } catch (e: Exception) {
                    reportException(e)
                } finally {
                    _isRefreshing.value = false
                }
            }
        }

        fun syncLikedSongs() {
            refresh()
        }
    }

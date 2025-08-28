package com.example.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.favorites.domain.FavoritesRepository
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.playlists.domain.StringProvider
import com.example.playlistmaker.playlists.utils.WordUtils
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch
import com.example.playlistmaker.toTrack

class PlaylistContentViewModel(
    private val repository: PlaylistRepository,
    private val favoritesRepository: FavoritesRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> = _totalDuration

    private val _trackCountWord = MutableLiveData<String>()
    val trackCountWord: LiveData<String> = _trackCountWord

    private val _trackCount = MutableLiveData<String>()
    val trackCount: LiveData<String> = _trackCount

    private val _playlist = MutableLiveData<PlaylistEntity?>()
    val playlist: LiveData<PlaylistEntity?> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val playlist = repository.getPlaylistById(id)
            _playlist.postValue(playlist)

            playlist?.let {
                // Получаем список PlaylistTrackEntity для этого плейлиста
                val playlistTracks = repository.getTracksForPlaylist(it)
                    .sortedByDescending { track -> track.addedAt }

                val favoriteIds = favoritesRepository.getAllFavoriteTrackIdsOnce().toSet()

                val mappedTracks = playlistTracks.map { it.toTrack() }

                val tracks = mappedTracks.map { track ->
                    track.copy(isFavorite = favoriteIds.contains(track.trackId))
                }

                _tracks.postValue(tracks)

                val totalDurationMillis = tracks.sumOf { it.trackTimeMillis.toLong() }
                _totalDuration.postValue(totalDurationMillis)

                val test = WordUtils.getTrackWord(tracks.size, stringProvider)

                val resString = "${tracks.size} ${WordUtils.getTrackWord(tracks.size, stringProvider)}"
                _trackCount.postValue(resString)
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(track.trackId.toString(), playlistId)
            loadPlaylist(playlistId) // Обновим список после удаления
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
        }
    }
}

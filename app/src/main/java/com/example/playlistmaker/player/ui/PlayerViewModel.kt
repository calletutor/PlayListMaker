package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.favorites.domain.FavoritesInteractor
import com.example.playlistmaker.player.domain.MusicService
import com.example.playlistmaker.player.domain.MusicServiceController
import com.example.playlistmaker.playlists.data.toEntity
import com.example.playlistmaker.playlists.domain.AddTrackToPlaylistUseCase
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val defaultPlayTime: String,
    private val favoritesInteractor: FavoritesInteractor,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(PlayerScreenState(playTime = defaultPlayTime))
    val uiState: LiveData<PlayerScreenState> = _uiState

    val successEvent = MutableLiveData<String?>()
    val errorEvent = MutableLiveData<String?>()
    val toastMessage = MutableLiveData<String?>()
    val playlists = MutableLiveData<List<PlaylistEntity>>()

    var currentTrack: Track? = null
    var musicService: MusicServiceController? = null
//    var musicService: MusicService? = null

    fun bindService(service: MusicServiceController) {
//    fun bindService(service: MusicService) {
        musicService = service
        service.isPlayingLiveData.observeForever { isPlaying ->
            _uiState.postValue(_uiState.value?.copy(isPlaying = isPlaying))
        }
        service.currentTimeLiveData.observeForever { time ->
            _uiState.postValue(_uiState.value?.copy(playTime = time))
        }
    }

    fun unbindService() {
        musicService = null
    }


    fun preparePlayer(track: Track) {
        currentTrack = track

        // передаём трек в сервис
        musicService?.setCurrentTrack(track)

        viewModelScope.launch {
            val isFav = favoritesInteractor.isTrackFavorite(track.trackId)
            _uiState.value = _uiState.value?.copy(isFavorite = isFav)
        }

        track.previewUrl?.let { url ->
            musicService?.preparePlayer(
                url,
                onPrepared = { /* можно обновить кнопку play */ },
                onCompletion = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            playTime = defaultPlayTime,
                            isPlaying = false,
                            buttonResId = R.drawable.play_button
                        )
                    )
                },
                track = track // теперь сюда тоже идёт трек
            )
        }
    }



    fun playbackControl() {
        musicService?.togglePlayer()
    }

    fun releasePlayer() {
        musicService?.releasePlayer()
    }

    fun toggleFavorite(track: Track) {
        val newState = !(_uiState.value?.isFavorite ?: false)
        _uiState.value = _uiState.value?.copy(isFavorite = newState)
        track.isFavorite = newState
        viewModelScope.launch {
            if (newState) favoritesInteractor.addToFavorites(track)
            else favoritesInteractor.removeFromFavorites(track)
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists().collect { list ->
                playlists.postValue(list)
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long) {
        val track = currentTrack ?: run {
            errorEvent.postValue("Ошибка: трек не найден")
            return
        }
        viewModelScope.launch {
            val added = playlistRepository.addTrackToPlaylist(playlistId, track.trackId, track.toEntity())
            if (added) successEvent.postValue("Трек успешно добавлен в плейлист")
            else errorEvent.postValue("Трек уже есть в плейлисте")
        }
    }

    fun clearToastMessage() { toastMessage.value = null }
    fun clearSuccessMessage() { successEvent.value = null }
    fun clearErrorMessage() { errorEvent.value = null }


    fun onUIStart() {
        musicService?.hideNotification()
    }

    fun onUIStop() {
        musicService?.showNotification()
    }



}

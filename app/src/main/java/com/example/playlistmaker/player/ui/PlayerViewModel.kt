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
import com.example.playlistmaker.playlists.data.toEntity
import com.example.playlistmaker.playlists.domain.AddTrackToPlaylistUseCase
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerViewModel(

    private val playerInteractor: PlayerInteractor,
    private val defaultPlayTime: String,
    private val favoritesInteractor: FavoritesInteractor,

    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase,
    private val playlistRepository: PlaylistRepository


) : ViewModel() {

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    companion object {

        private const val DELAY = 300L
    }

    private val _uiState = MutableLiveData(
        PlayerScreenState(
            playTime = defaultPlayTime
        )
    )

    private val _successEvent = MutableLiveData<String?>()
    val successEvent: LiveData<String?> = _successEvent

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _playlists = MutableLiveData<List<PlaylistEntity>>()
    val playlists: LiveData<List<PlaylistEntity>> = _playlists

    val uiState: LiveData<PlayerScreenState> = _uiState

    private var playerState = PlayerState.DEFAULT
    private var timerJob: Job? = null
    var currentTrack: Track? = null

    fun preparePlayer(track: Track) {

        _uiState.value = _uiState.value?.copy(
            isFavorite = track.isFavorite
        )

        currentTrack = track
        track.previewUrl?.let {
            playerInteractor.preparePlayer(
                it,
                onPrepared = {
                    playerState = PlayerState.PREPARED
                    _uiState.postValue(_uiState.value?.copy(isPrepared = true))
                },
                onCompletion = {
                    playerState = PlayerState.PREPARED
                    timerJob?.cancel()
                    _uiState.postValue(
                        _uiState.value?.copy(
                            playTime = defaultPlayTime,
                            isPlaying = false,
                            buttonResId = R.drawable.play_button
                        )
                    )
                }
            )
        }
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerState = PlayerState.PLAYING
        _uiState.value = _uiState.value?.copy(
            isPlaying = true,
            buttonResId = R.drawable.pause_button_light
        )
        timerJob?.cancel()
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState = PlayerState.PAUSED
        _uiState.value = _uiState.value?.copy(
            isPlaying = false,
            buttonResId = R.drawable.play_button
        )
        timerJob?.cancel()
    }


    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {

            while (isActive) {
                if (playerState == PlayerState.PLAYING) {

                    val currentPosition = playerInteractor.getCurrentPosition()

                    _uiState.postValue(
                        _uiState.value?.copy(

                            playTime = formatTime(currentPosition / 1000)

                        )
                    )
                }
                delay(DELAY)
            }
        }
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        timerJob?.cancel()
    }

    fun toggleFavorite(track: Track) {
        val isCurrentlyFavorite = _uiState.value?.isFavorite ?: false
        val newFavoriteState = !isCurrentlyFavorite

        _uiState.value = _uiState.value?.copy(isFavorite = newFavoriteState)

        track.isFavorite = newFavoriteState

        viewModelScope.launch {
            if (newFavoriteState) {
                favoritesInteractor.addToFavorites(track)
            } else {
                favoritesInteractor.removeFromFavorites(track)
            }
        }
    }


    fun loadPlaylists() {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists()
                .collect { playlists ->
                    _playlists.postValue(playlists)
                }
        }
    }


    fun addTrackToPlaylist(playlistId: Long) {
        val track = currentTrack
        if (track == null) {
            _errorEvent.postValue("Ошибка: трек не найден")
            return
        }

        viewModelScope.launch {
            val wasAdded = playlistRepository.addTrackToPlaylist(
                playlistId,
                track.trackId,
                track.toEntity()
            )
            if (wasAdded) {
                _successEvent.postValue("Трек успешно добавлен в плейлист")
            } else {
                _errorEvent.postValue("Трек уже есть в плейлисте")
            }
        }


    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }


    fun clearSuccessMessage() {
        _successEvent.value = null
    }

    fun clearErrorMessage() {
        _errorEvent.value = null
    }

}

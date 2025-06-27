package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 300L
    }

    private val _uiState = MutableLiveData(PlayerScreenState())
    val uiState: LiveData<PlayerScreenState> = _uiState

    private var playerState = STATE_DEFAULT
    private var timerJob: Job? = null
    private var totalPlayTimeElapsed = 0
    var currentTrack: Track? = null

    fun preparePlayer(track: Track) {
        currentTrack = track
        track.previewUrl?.let {
            playerInteractor.preparePlayer(
                it,
                onPrepared = {
                    playerState = STATE_PREPARED
                    _uiState.postValue(_uiState.value?.copy(isPrepared = true))
                },
                onCompletion = {
                    playerState = STATE_PREPARED
                    totalPlayTimeElapsed = 0
                    timerJob?.cancel()
                    _uiState.postValue(
                        _uiState.value?.copy(
                            playTime = "00:00",
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
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerState = STATE_PLAYING
        _uiState.value = _uiState.value?.copy(
            isPlaying = true,
            buttonResId = R.drawable.pause_button_light
        )
        timerJob?.cancel()
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState = STATE_PAUSED
        _uiState.value = _uiState.value?.copy(
            isPlaying = false,
            buttonResId = R.drawable.play_button
        )
        timerJob?.cancel()
    }



    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            var lastUpdateTime = System.currentTimeMillis()

            while (isActive) {
                if (playerState == STATE_PLAYING) {
                    val currentTime = System.currentTimeMillis()
                    val elapsed = (currentTime - lastUpdateTime).toInt()

                    totalPlayTimeElapsed += elapsed
                    lastUpdateTime = currentTime

                    _uiState.postValue(
                        _uiState.value?.copy(
                            playTime = formatTime(totalPlayTimeElapsed / 1000)
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
}

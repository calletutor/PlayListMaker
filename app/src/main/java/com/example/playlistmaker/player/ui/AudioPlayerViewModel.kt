package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.Track

class AudioPlayerViewModel : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    private var totalPlayTimeElapsed = 0

    val playTimeLiveData = MutableLiveData<String>()
    val playerButtonStateLiveData = MutableLiveData<Int>() // Resource ID для кнопки
    val playerPreparedLiveData = MutableLiveData<Boolean>()

    var currentTrack: Track? = null

    fun preparePlayer(track: Track) {
        currentTrack = track
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            playerPreparedLiveData.postValue(true)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playerButtonStateLiveData.postValue(R.drawable.play_button)
        }
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playerButtonStateLiveData.postValue(R.drawable.pause_button_light)
        if (timerRunnable == null) {
            startTimer()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playerButtonStateLiveData.postValue(R.drawable.play_button)
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    totalPlayTimeElapsed++
                    playTimeLiveData.postValue(formatTime(totalPlayTimeElapsed))
                }
                timerHandler.postDelayed(this, DELAY)
            }
        }
        timerHandler.postDelayed(timerRunnable!!, DELAY)
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        timerHandler.removeCallbacksAndMessages(null)
    }
}

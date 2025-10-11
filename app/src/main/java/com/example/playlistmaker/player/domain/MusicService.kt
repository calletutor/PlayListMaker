package com.example.playlistmaker.player.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.data.PlayerWrapper
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.*


class MusicService : Service(), MusicServiceController {

    companion object {
        const val CHANNEL_ID = "MusicPlayerChannel"
        private const val NOTIFICATION_ID = 1
        private const val UPDATE_DELAY = 300L
    }

    private var currentTrack: Track? = null
    private val binder = MusicServiceBinder()
    private val playerWrapper = PlayerWrapper()
    private val playerRepository = PlayerRepositoryImpl(playerWrapper)
    private val playerInteractor = PlayerInteractorImpl(playerRepository)

    enum class PlayerState { DEFAULT, PREPARED, PLAYING, PAUSED }

    private var playerState = PlayerState.DEFAULT
    private var timerJob: Job? = null

    override val isPlayingLiveData = MutableLiveData(false)
    override val currentTimeLiveData = MutableLiveData("00:00")


    inner class MusicServiceBinder : Binder() {
        fun getController(): MusicServiceController = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun preparePlayer(
        url: String,
        onPrepared: (() -> Unit)?,
        onCompletion: (() -> Unit)?,
        track: Track
    ) {
        setCurrentTrack(track)

        playerInteractor.preparePlayer(
            url,
            onPrepared = {
                playerState = PlayerState.PREPARED
                onPrepared?.invoke()
            },
            onCompletion = {
                playerState = PlayerState.PREPARED
                timerJob?.cancel()
                currentTimeLiveData.postValue("00:00")
                isPlayingLiveData.postValue(false)
                stopForeground(true)
                onCompletion?.invoke()
            },
        )
    }

    override fun startPlayer() {
        if (playerState == PlayerState.PREPARED || playerState == PlayerState.PAUSED) {
            playerInteractor.startPlayer()
            playerState = PlayerState.PLAYING
            isPlayingLiveData.postValue(true)

            startTimer()
        }
    }

    override fun pausePlayer() {
        if (playerState == PlayerState.PLAYING) {
            playerInteractor.pausePlayer()
            playerState = PlayerState.PAUSED
            isPlayingLiveData.postValue(false)
            timerJob?.cancel()
        }
    }

    override fun togglePlayer() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PAUSED, PlayerState.PREPARED -> startPlayer()
            else -> {}
        }
    }

    override fun releasePlayer() {
        playerInteractor.release()
        timerJob?.cancel()
        stopForeground(true)
        stopSelf()
    }

    override fun getCurrentPosition(): Int = playerInteractor.getCurrentPosition()

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (playerState == PlayerState.PLAYING) {
                    val pos = getCurrentPosition() / 1000
                    currentTimeLiveData.postValue(formatTime(pos))
                }
                delay(UPDATE_DELAY)
            }
        }
    }

    private fun formatTime(seconds: Int): String =
        String.format("%02d:%02d", seconds / 60, seconds % 60)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {

        val title = currentTrack?.trackName ?: "PlayListMaker"
        val text = currentTrack?.artistName ?: "Playing music"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(String.format("%s - %s", title, text))
            .setSmallIcon(R.drawable.baseline_crop_square_24)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "SHOW_NOTIFICATION" -> {
                if (playerState == PlayerState.PLAYING) {
                    startForeground(NOTIFICATION_ID, createNotification())
                }
            }

            "HIDE_NOTIFICATION" -> {
                stopForeground(true)
            }
        }
        return START_NOT_STICKY
    }


    override fun showNotification() {
        if (playerState == PlayerState.PLAYING) {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }

    override fun hideNotification() {
        stopForeground(true)
    }

    override fun setCurrentTrack(track: Track) {
        currentTrack = track
    }
}

package com.example.playlistmaker.player.domain

import androidx.lifecycle.LiveData
import com.example.playlistmaker.search.domain.Track

interface MusicServiceController {
    val isPlayingLiveData: LiveData<Boolean>
    val currentTimeLiveData: LiveData<String>

    fun preparePlayer(url: String, onPrepared: (() -> Unit)? = null, onCompletion: (() -> Unit)? = null, track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun togglePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int

    // Новые методы для foreground-уведомления
    fun showNotification()
    fun hideNotification()

    fun setCurrentTrack(track: Track)

}

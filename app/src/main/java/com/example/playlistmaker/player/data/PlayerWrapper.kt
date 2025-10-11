package com.example.playlistmaker.player.data

import android.media.AudioAttributes
import android.media.MediaPlayer


class PlayerWrapper {

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                onPrepared()
            }
            setOnCompletionListener { onCompletion() }
            prepareAsync()
        }
    }

    fun start() {
        if (isPrepared) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        if (isPrepared && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun isPlaying(): Boolean = isPrepared && mediaPlayer?.isPlaying ?: false

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    fun getCurrentPosition(): Int = if (isPrepared) mediaPlayer?.currentPosition ?: 0 else 0
}

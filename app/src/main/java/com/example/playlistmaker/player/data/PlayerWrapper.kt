package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class PlayerWrapper {

    private var mediaPlayer: MediaPlayer? = null
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        release() // освобождаем если был старый

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                onPreparedCallback = null
                onPrepared()
            }
            setOnCompletionListener {
                onCompletionCallback = null
                onCompletion()
            }
            prepareAsync()
        }
        onPreparedCallback = onPrepared
        onCompletionCallback = onCompletion
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

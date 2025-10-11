package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.data.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {



    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        playerRepository.prepare(url, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        playerRepository.start()
    }

    override fun pausePlayer() {
        playerRepository.pause()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override fun release() {
        playerRepository.release()
    }



}

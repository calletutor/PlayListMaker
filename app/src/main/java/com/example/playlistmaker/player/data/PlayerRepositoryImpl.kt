package com.example.playlistmaker.player.data

class PlayerRepositoryImpl(
    private val mediaPlayerWrapper: PlayerWrapper
) : PlayerRepository {
    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayerWrapper.prepare(url, onPrepared, onCompletion)
    }

    override fun start() = mediaPlayerWrapper.start()
    override fun pause() = mediaPlayerWrapper.pause()
    override fun isPlaying() = mediaPlayerWrapper.isPlaying()
    override fun release() = mediaPlayerWrapper.release()
}

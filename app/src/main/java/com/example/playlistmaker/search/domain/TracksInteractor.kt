package com.example.playlistmaker.search.domain


interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TracksResult)
    }
}

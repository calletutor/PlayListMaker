package com.example.playlistmaker.playlists.domain

import com.example.playlistmaker.favorites.data.db.TrackEntity

class AddTrackToPlaylistUseCase(
    private val repository: PlaylistRepository
) {


    suspend operator fun invoke(playlistId: Long, trackId: Int, trackData: TrackEntity?) {
        repository.addTrackToPlaylist(playlistId, trackId, trackData)
    }


}



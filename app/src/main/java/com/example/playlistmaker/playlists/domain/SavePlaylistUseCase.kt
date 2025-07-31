package com.example.playlistmaker.playlists.domain

import com.example.playlistmaker.favorites.data.db.PlaylistEntity

class SavePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: PlaylistEntity) {
        repository.insertPlaylist(playlist)
    }
}

package com.example.playlistmaker.playlists.domain

import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylistById(id: Long): PlaylistEntity?
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Int, trackData: TrackEntity?): Boolean

    /////////////////////////////////////////////////
    //для тестирования начало
    suspend fun getTracksForPlaylist(playlist: PlaylistEntity): List<PlaylistTrackEntity>
    //для тестирования окончание
    /////////////////////////////////////////////////

    suspend fun getPlaylistByName(name: String): PlaylistEntity?

}

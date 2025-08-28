package com.example.playlistmaker.playlists.data

import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.favorites.data.db.TrackDao
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.playlist.data.db.PlaylistDao
import com.example.playlistmaker.playlist.data.db.PlaylistTrackDao
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.playlists.data.db.PlaylistTrackCrossRef
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.toTrack


class PlaylistRepositoryImpl(

    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val gson: Gson

) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun insertPlaylist(playlist: PlaylistEntity) {
        playlistDao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Long): PlaylistEntity? {
        return playlistDao.getPlaylistById(id)
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }


    override suspend fun getTracksForPlaylist(playlist: PlaylistEntity): List<PlaylistTrackEntity> {
        return playlistTrackDao.getPlaylistWithTracks(playlist.playlistId)?.tracks ?: emptyList()
    }


    override suspend fun getPlaylistByName(name: String): PlaylistEntity? {
        return playlistDao.getPlaylistByName(name)
    }


    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Int,
        trackData: TrackEntity?
    ): Boolean {
        val existingTrack = playlistTrackDao.getTrackById(trackId)
        if (existingTrack == null && trackData != null) {
            val playlistTrackEntity = trackData.toPlaylistTrackEntity()
            playlistTrackDao.insertTrack(playlistTrackEntity)
        }

        val crossRef = PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId)
        val result = playlistTrackDao.insertCrossRef(crossRef)

        val wasTrackAdded = result != -1L
        if (wasTrackAdded) {
            val playlistWithTracks = playlistTrackDao.getPlaylistWithTracks(playlistId)
            val updatedTrackCount = playlistWithTracks?.tracks?.size ?: 0
            playlistWithTracks?.playlist?.copy(tracksCount = updatedTrackCount)
                ?.let { updatedPlaylist ->
                    playlistDao.updatePlaylist(updatedPlaylist)
                }
        }

        return wasTrackAdded
    }

    override suspend fun getTracksByIds(ids: List<Long>): List<Track> {
        return trackDao.getTracksByIds(ids.map { it.toInt() }).map { it.toTrack() }
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Long) {


        // Преобразуем trackId в Int, если требуется, чтобы соответствовать типу данных в crossRef.
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = trackId.toInt()
        )

        // Удаляем связь между плейлистом и треком.
        playlistTrackDao.deleteCrossRef(crossRef)

        // После удаления связи получаем обновленные данные плейлиста вместе с его треками.
        val playlistWithTracks = playlistTrackDao.getPlaylistWithTracks(playlistId)
        val updatedTrackCount = playlistWithTracks?.tracks?.size ?: 0

        // Обновляем счетчик треков в плейлисте, создавая копию сущности с новым значением.
        playlistWithTracks?.playlist?.copy(tracksCount = updatedTrackCount)?.let { updatedPlaylist ->
            playlistDao.updatePlaylist(updatedPlaylist)
        }
    }

}

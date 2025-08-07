package com.example.playlistmaker.playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.playlists.data.db.PlaylistTrackCrossRef
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity
import com.example.playlistmaker.playlists.data.db.PlaylistWithTracks
import com.example.playlistmaker.playlists.data.db.TrackWithPlaylists

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): PlaylistTrackEntity?

    ////////////////////////////////////////////////////
    //для тестирования начало
    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Int>): List<PlaylistTrackEntity>
    //для тестирования окончание
    ////////////////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef):Long

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks?

    @Transaction
    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackWithPlaylists(trackId: Int): TrackWithPlaylists?

}

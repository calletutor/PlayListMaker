package com.example.playlistmaker.favorites.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM favorite_tracks WHERE trackId = :trackId LIMIT 1")
    suspend fun getTrackById(trackId: Int): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: TrackEntity)

    @Delete
    suspend fun removeFromFavorites(track: TrackEntity)

    @Query("SELECT trackId FROM favorite_tracks")
    fun getAllFavoriteTrackIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    fun getAllFavoriteTracksSorted(): Flow<List<TrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId)")
    suspend fun isTrackFavorite(trackId: Int): Boolean

}

package com.example.playlistmaker.A_NEW.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: TrackEntity)

    @Delete
    suspend fun removeFromFavorites(track: TrackEntity)

    @Query("SELECT trackId FROM favorite_tracks")
    fun getAllFavoriteTrackIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    fun getAllFavoriteTracksSorted(): Flow<List<TrackEntity>>

}

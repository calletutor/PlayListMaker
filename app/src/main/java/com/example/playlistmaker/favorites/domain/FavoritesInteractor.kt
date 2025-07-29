package com.example.playlistmaker.favorites.domain

import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>
    fun getAllFavoriteTrackIds(): Flow<List<Int>>
    suspend fun isTrackFavorite(trackId: Int): Boolean
}

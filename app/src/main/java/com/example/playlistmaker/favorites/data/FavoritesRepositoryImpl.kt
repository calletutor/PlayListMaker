package com.example.playlistmaker.favorites.data

import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.favorites.data.db.TracksDatabase
import com.example.playlistmaker.favorites.domain.FavoritesRepository
//import com.example.playlistmaker.favorites.domain.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesRepositoryImpl(
    private val database: TracksDatabase
) : FavoritesRepository {

    private val trackDao by lazy { database.trackDao() }

    override suspend fun addToFavorites(track: TrackEntity) {
        trackDao.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: TrackEntity) {
        trackDao.removeFromFavorites(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<TrackEntity>> {
        return trackDao.getAllFavoriteTracksSorted()
    }

    override fun getAllFavoriteTrackIds(): Flow<List<Int>> {
        return trackDao.getAllFavoriteTrackIds()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return trackDao.isTrackFavorite(trackId)
    }

    override suspend fun getAllFavoriteTrackIdsOnce(): List<Int> {
        return trackDao.getAllFavoriteTrackIdsOnce()
    }

}

package com.example.playlistmaker.A_NEW.data

import com.example.playlistmaker.A_NEW.data.db.TrackEntity
import com.example.playlistmaker.A_NEW.data.db.TracksDatabase
import com.example.playlistmaker.A_NEW.domain.FavoritesRepository
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
}

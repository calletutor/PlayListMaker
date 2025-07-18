package com.example.playlistmaker.search.data

import android.util.Log
import com.example.playlistmaker.A_NEW.data.db.TracksDatabase
import com.example.playlistmaker.search.domain.SearchTracksResult
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SearchTracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val database: TracksDatabase
) : SearchTracksRepository {

    override fun searchTracks(expression: String): Flow<SearchTracksResult> = flow {
        try {
            val response = withContext(Dispatchers.IO) {
                networkClient.doRequest(TracksSearchRequest(expression))
            }

            val result = if (response.resultCode == 200 && response is TracksSearchResponse) {
                val tracks = trackMapper.map(response)
                SearchTracksResult(tracks = tracks, isSuccess = true, isNetworkError = false)
            } else {
                SearchTracksResult(emptyList(), isSuccess = false, isNetworkError = true)
            }

            if (result.tracks.isNotEmpty()) {


                val localTracks =
                    database.trackDao().getAllFavoriteTracksSorted().firstOrNull() ?: emptyList()

                for (item in result.tracks) {

                    item.isFavorite = localTracks.any { it.trackId == item.trackId }

                }

            }

            emit(result)

        } catch (e: IOException) {
            emit(SearchTracksResult(emptyList(), isSuccess = false, isNetworkError = true))
        } catch (e: Exception) {
            emit(SearchTracksResult(emptyList(), isSuccess = false, isNetworkError = true))
        }
    }
}

package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksResult
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<TracksResult> = flow {
        try {
            val response = withContext(Dispatchers.IO) {
                networkClient.doRequest(TracksSearchRequest(expression))
            }

            val result = if (response.resultCode == 200 && response is TracksSearchResponse) {
                val tracks = trackMapper.map(response)
                TracksResult(tracks = tracks, isSuccess = true, isNetworkError = false)
            } else {
                TracksResult(emptyList(), isSuccess = false, isNetworkError = true)
            }

            emit(result)

        } catch (e: IOException) {
            emit(TracksResult(emptyList(), isSuccess = false, isNetworkError = true))
        } catch (e: Exception) {
            emit(TracksResult(emptyList(), isSuccess = false, isNetworkError = true))
        }
    }
}


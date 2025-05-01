package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksResult
import com.example.playlistmaker.search.domain.NetworkClient
import com.example.playlistmaker.player.domain.TrackMapper
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTracks(expression: String): TracksResult {
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))

            if (response.resultCode == 200 && response is TracksSearchResponse) {
                val tracks = trackMapper.map(response)
                TracksResult(tracks = tracks, isSuccess = true, isNetworkError = false)
            } else {
                TracksResult(emptyList(), isSuccess = false, isNetworkError = true)
            }

        } catch (e: IOException) {
            TracksResult(emptyList(), isSuccess = false, isNetworkError = true)
        } catch (e: Exception) {
            TracksResult(emptyList(), isSuccess = false, isNetworkError = true)
        }
    }
}


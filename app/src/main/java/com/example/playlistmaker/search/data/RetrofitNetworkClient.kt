package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitNetworkClient : NetworkClient {

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApi::class.java)

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TracksSearchRequest) {
            try {
                val result = imdbService.searchTracks(dto.expression)
                result.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = 500 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}

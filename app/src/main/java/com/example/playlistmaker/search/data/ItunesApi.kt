package com.example.playlistmaker.search.data

import retrofit2.http.GET
import retrofit2.http.Query


interface ItunesApi {

    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): TracksSearchResponse

}

package com.example.playlistmaker.search.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TracksSearchResponse>

}

package com.example.playlistmaker.data.dto

data class TrackDto(
    val previewUrl: String?,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val country: String,
    val primaryGenreName: String,
)

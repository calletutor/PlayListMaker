package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Track(
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
) : Parcelable

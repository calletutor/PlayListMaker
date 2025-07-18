package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import com.example.playlistmaker.A_NEW.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}


fun TrackEntity.toTrack(): Track = Track(
    trackId = this.trackId,
    isFavorite = this.isFavorite,
    previewUrl = this.previewUrl,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTimeMillis = this.trackTimeMillis,
    artworkUrl100 = this.artworkUrl100,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    country = this.country,
    primaryGenreName = this.primaryGenreName
)


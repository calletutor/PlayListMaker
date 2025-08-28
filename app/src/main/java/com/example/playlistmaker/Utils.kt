package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity
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

fun PlaylistTrackEntity.toTrack(): Track = Track(
    trackId = this.trackId,
    isFavorite = this.isFavorite,          // или взять из другой сущности, если есть
//    isFavorite = false,          // или взять из другой сущности, если есть
    previewUrl = this.previewUrl,           // если в PlaylistTrackEntity нет — можно null
//    previewUrl = null,           // если в PlaylistTrackEntity нет — можно null
    trackName = this.trackName,
    artistName = this.artistName,
    trackTimeMillis = this.trackTimeMillis,
    artworkUrl100 = this.artworkUrl100,
    collectionName = "",       // если есть — подставить, иначе null
    releaseDate = "",          // если есть — подставить, иначе null
    country = "",              // если есть — подставить, иначе null
    primaryGenreName = ""      // если есть — подставить, иначе null
)

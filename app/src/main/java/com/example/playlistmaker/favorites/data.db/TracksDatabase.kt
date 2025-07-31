package com.example.playlistmaker.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TrackEntity::class], version = 1)
abstract class TracksDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

}

package com.example.playlistmaker.A_NEW.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [TrackEntity::class], version = 1)
abstract class TracksDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: TracksDatabase? = null

        fun getDatabase(context: Context): TracksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TracksDatabase::class.java,
                    "tracks_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

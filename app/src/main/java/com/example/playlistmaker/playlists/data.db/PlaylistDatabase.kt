package com.example.playlistmaker.playlist.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.favorites.data.db.TrackDao
import com.example.playlistmaker.playlists.data.Converters
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.playlists.data.db.PlaylistTrackCrossRef


@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        PlaylistTrackCrossRef::class
    ], version = 4
)
@TypeConverters(Converters::class)

//@Database(entities = [TrackEntity::class, PlaylistEntity::class], version = 2)
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        @Volatile
        private var INSTANCE: PlaylistDatabase? = null

        fun getDatabase(context: Context): PlaylistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaylistDatabase::class.java,
                    "playlist_database"
                ).fallbackToDestructiveMigration() // для обновления схемы без миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

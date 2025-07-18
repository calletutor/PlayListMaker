package com.example.playlistmaker.A_NEW.DI

import androidx.room.Room
import com.example.playlistmaker.A_NEW.data.db.TrackDao
import com.example.playlistmaker.A_NEW.data.db.TracksDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TracksDatabase::class.java,
            "tracks_database"
        ).build()
    }

    single<TrackDao> {
        get<TracksDatabase>().trackDao()
    }
}

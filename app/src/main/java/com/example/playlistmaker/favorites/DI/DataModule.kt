package com.example.playlistmaker.favorites.DI

import androidx.room.Room
import com.example.playlistmaker.favorites.data.db.TrackDao
import com.example.playlistmaker.favorites.data.db.TracksDatabase
import com.example.playlistmaker.media.ui.ViewModelSelectedTracks
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
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

val viewModelModule = module {
    viewModel { ViewModelSelectedTracks(get()) }
}

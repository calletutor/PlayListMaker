package com.example.playlistmaker.playlists.di

import com.example.playlistmaker.playlists.ui.NewPlaylistViewModel
import com.example.playlistmaker.playlist.data.db.PlaylistDao
import com.example.playlistmaker.playlist.data.db.PlaylistDatabase
import com.example.playlistmaker.playlists.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlists.domain.AddTrackToPlaylistUseCase
import com.example.playlistmaker.playlists.domain.ImageStorageManager
import com.example.playlistmaker.playlists.domain.ImageStorageManagerImpl
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.playlists.domain.ResourceStringProvider
import com.example.playlistmaker.playlists.domain.SavePlaylistUseCase
import com.example.playlistmaker.playlists.domain.StringProvider
import com.example.playlistmaker.playlists.domain.StringProviderImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module



val playlistModule = module {

    single<PlaylistRepository> { PlaylistRepositoryImpl(get(),get(),get(),get()) }

    single { SavePlaylistUseCase(get()) }

    viewModel { NewPlaylistViewModel(get(), get(), get(), get()) }

    single { AddTrackToPlaylistUseCase(get()) }

    single { get<PlaylistDatabase>().playlistTrackDao() }

    single<ImageStorageManager> { ImageStorageManagerImpl(androidContext()) }


    single<StringProvider> { StringProviderImpl(androidContext()) }

}

val playListDatabaseModule = module {
    single {
        PlaylistDatabase.getDatabase(androidContext())
    }

    single<PlaylistDao> {
        get<PlaylistDatabase>().playlistDao()
    }


}

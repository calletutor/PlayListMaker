package com.example.playlistmaker.DI

import com.example.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.favorites.domain.FavoritesRepository
import com.example.playlistmaker.main.ui.SETTINGS_PREFS
import com.example.playlistmaker.main.ui.TRACK_HISTORY
import com.example.playlistmaker.player.data.PlayerRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.SearchTracksRepository
import com.example.playlistmaker.search.data.SearchTracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

object RepositoryModule {

    val repositoryModule = module {

        single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }// get() автоматически предоставит TracksDatabase
        single<SearchTracksRepository> { SearchTracksRepositoryImpl(get(), get(), get()) }
        single<SettingsRepository> { SettingsRepositoryImpl(get(named(SETTINGS_PREFS))) }
        single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(named(TRACK_HISTORY)), get()) }
        single<PlayerRepository> { PlayerRepositoryImpl(get()) }

    }
}

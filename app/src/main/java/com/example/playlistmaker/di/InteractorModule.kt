package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.domain.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.FavoritesInteractorImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl

object InteractorModule {

    val interactorModule = module {
        single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
        single<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
        single<SettingsInteractor> { SettingsInteractorImpl(get()) }
        single<SharingInteractor> { SharingInteractorImpl(get(), get()) }
        single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get(), get()) }
        single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    }
}

package com.example.playlistmaker.DI

import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl

object InteractorModule {

    val interactorModule = module {
        single<TracksInteractor> { TracksInteractorImpl(get()) }
        single<SettingsInteractor> { SettingsInteractorImpl(get()) }
        single<SharingInteractor> { SharingInteractorImpl(get(), get()) }
        single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
        single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    }

}

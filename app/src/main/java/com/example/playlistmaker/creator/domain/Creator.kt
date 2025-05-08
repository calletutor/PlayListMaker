package com.example.playlistmaker.creator.domain

import android.app.Activity
import com.example.playlistmaker.search.data.TracksRepository
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.ui.PlayerViewModelFactory
import com.example.playlistmaker.player.data.PlayerWrapper
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.data.PlayerRepository
import com.example.playlistmaker.main.ui.TRACK_HISTORY
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.AppLinkProviderImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.gson.Gson
import com.example.playlistmaker.player.data.PlayerRepositoryImpl


object Creator {

    private lateinit var application: Application
    private lateinit var appLinkProvider: AppLinkProvider
    private lateinit var externalNavigator: ExternalNavigator

    fun initApplication(app: Application) {
        application = app
        appLinkProvider = AppLinkProviderImpl(application)
        externalNavigator = ExternalNavigatorImpl(application)
    }

    private val gson by lazy { Gson() }

    private val trackMapper by lazy { TrackMapper() }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(),
            trackMapper = trackMapper
        )
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideHistorySharedPreferences(), gson)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun provideHistorySharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
    }

    private fun provideSettingsSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provideSettingsSharedPreferences())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(externalNavigator, appLinkProvider)
    }

    private val mediaPlayerWrapper: PlayerWrapper by lazy {
        PlayerWrapper()
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl(mediaPlayerWrapper)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(playerRepository)
    }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        val interactor: PlayerInteractor = providePlayerInteractor()
        return PlayerViewModelFactory(interactor)
    }

}

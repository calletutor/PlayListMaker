package com.example.playlistmaker.DI

import android.content.Context
import android.content.res.Resources
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.SETTINGS_PREFS
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.example.playlistmaker.main.ui.TRACK_HISTORY
import com.example.playlistmaker.player.data.PlayerRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.data.PlayerWrapper
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.data.ItunesApi
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackMapper
import com.example.playlistmaker.search.data.SearchTracksRepository
import com.example.playlistmaker.search.data.SearchTracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.SearchTracksInteractor
import com.example.playlistmaker.search.domain.SearchTracksInteractorImpl
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.sharing.data.AppLinkProviderImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataModule {

    val dataModule = module {

        single<NetworkClient> { RetrofitNetworkClient() }
        single {
            Retrofit.Builder()
                .baseUrl("https://itunes.apple.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        single { get<Retrofit>().create(ItunesApi::class.java) }

        single {
            androidContext().resources
        }

        single(named(SETTINGS_PREFS)) {
            get<Context>().getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        }

        single(named(TRACK_HISTORY)) {
            get<Context>().getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        }

        single<SearchTracksRepository> { SearchTracksRepositoryImpl(get(), get(), get()) }
        single<SearchHistoryRepository> {
            SearchHistoryRepositoryImpl(
                get(named(TRACK_HISTORY)),
                get()
            )
        }
        single<SettingsRepository> { SettingsRepositoryImpl(get(named(SETTINGS_PREFS))) }
        single<PlayerRepository> { PlayerRepositoryImpl(get()) }

        single<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
        single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get(), get()) }
        single<SettingsInteractor> { SettingsInteractorImpl(get()) }
        single<PlayerInteractor> { PlayerInteractorImpl(get()) }
        single<SharingInteractor> {
            SharingInteractorImpl(
                externalNavigator = get(),
                appLinkProvider = get()
            )
        }

        single { Gson() }
        single { TrackMapper() }
        single { PlayerWrapper() }
        single { ExternalNavigatorImpl(get()) }
        single { AppLinkProviderImpl(get()) }

        viewModel { SearchViewModel(get(), get()) }

        viewModel {
            PlayerViewModel(
                 get(),
                 get<Resources>().getString(R.string.default_play_time),
                 get()
            )
        }

        viewModel { SettingsViewModel(get(), get()) }
    }
}

package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.AppLinkProviderImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

object NavigationModule {

    val navigationModule = module {
        single<ExternalNavigator> { ExternalNavigatorImpl(get()) }
        single<AppLinkProvider> { AppLinkProviderImpl(get()) }
    }
}

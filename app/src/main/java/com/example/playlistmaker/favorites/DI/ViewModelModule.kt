package com.example.playlistmaker.A_NEW.DI

import com.example.playlistmaker.A_NEW.ui.FavoritesViewModel
import com.example.playlistmaker.media.ui.ViewModelSelectedTracks
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { FavoritesViewModel(get()) }
    viewModel { ViewModelSelectedTracks(get()) }
}

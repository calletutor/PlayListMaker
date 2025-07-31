package com.example.playlistmaker.playlists.domain

import androidx.annotation.StringRes

interface StringProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: Any): String
}


package com.example.playlistmaker.playlists.domain

import android.content.Context
import androidx.annotation.StringRes

class ResourceStringProvider(private val context: Context) : StringProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}



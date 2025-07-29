package com.example.playlistmaker.playlists.domain

import android.content.Context

class StringProviderImpl(private val context: Context) : StringProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}

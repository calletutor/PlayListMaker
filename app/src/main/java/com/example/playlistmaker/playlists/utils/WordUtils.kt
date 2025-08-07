package com.example.playlistmaker.playlists.utils

import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.StringProvider

object WordUtils {
    fun getTrackWord(count: Int, stringProvider: StringProvider): String {
        val rem100 = count % 100
        val rem10 = count % 10

        return when {
            rem100 in 11..14 -> stringProvider.getString(R.string.track_word_form1)
            rem10 == 1 -> stringProvider.getString(R.string.track_word_form2)
            rem10 in 2..4 -> stringProvider.getString(R.string.track_word_form3)
            else -> stringProvider.getString(R.string.track_word_form1)
        }
    }
}


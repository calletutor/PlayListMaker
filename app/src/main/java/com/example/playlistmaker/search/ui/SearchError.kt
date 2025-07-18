package com.example.playlistmaker.search.ui

sealed class SearchError {
    object NothingFound : SearchError()
    object Network : SearchError()
    object Unknown : SearchError()
}

package com.example.playlistmaker.search.ui

sealed interface SearchError {
    object NothingFound : SearchError
    object Network : SearchError
    object Unknown : SearchError
}

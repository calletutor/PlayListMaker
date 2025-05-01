package com.example.playlistmaker.search.domain

interface NetworkClient {
    fun doRequest(dto: Any): Response
}

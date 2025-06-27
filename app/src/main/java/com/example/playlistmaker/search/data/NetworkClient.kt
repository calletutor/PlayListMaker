package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}

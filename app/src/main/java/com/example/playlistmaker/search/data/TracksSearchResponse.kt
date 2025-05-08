package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Response

class TracksSearchResponse(val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response()

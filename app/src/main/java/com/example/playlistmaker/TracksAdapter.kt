package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TracksAdapter(

    private val tracks: List<Track>

) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {

        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            var maxHistoryItems = 10

            val sharedPreferences =
                holder.itemView.context.getSharedPreferences("tracksHistory", Context.MODE_PRIVATE)
            val jsonString = sharedPreferences.getString("tracksHistory", null)

            var trackListType: Type?
            var trackListHistory: MutableList<Track>? = null // Инициализируем как null

            if (!jsonString.isNullOrEmpty()) {
                //истрория существует
                trackListType = object : TypeToken<MutableList<Track>>() {}.type
                trackListHistory = Gson().fromJson(jsonString, trackListType)

                if (!trackListHistory.isNullOrEmpty()) {

                    if (!trackListHistory.any { it.trackId == tracks[position].trackId }) {

                        trackListHistory.add(0, tracks[position])

                    } else {
                        //клик на уже сущестующем в истории треке. его нужно переместить наверх
                        trackListHistory.removeAt(trackListHistory.indexOfFirst { it.trackId == tracks[position].trackId })
                        trackListHistory.add(0, tracks[position])
                    }

                    while ((trackListHistory.size > maxHistoryItems)) {
                        trackListHistory.removeAt(trackListHistory.size - 1)
                    }

                    val jsonTracksHistory: String = Gson().toJson(trackListHistory)
                    sharedPreferences.edit()
                        .putString("tracksHistory", jsonTracksHistory)
                        .apply()
                }
            } else {
                //истории пока не было
                var trackListHistory: MutableList<Track> = mutableListOf()
                trackListHistory.add(tracks[position])
                val jsonTracksHistory: String = Gson().toJson(trackListHistory)
                sharedPreferences.edit()
                    .putString("tracksHistory", jsonTracksHistory)
                    .apply()
            }
        }
    }

    override fun getItemCount() = tracks.size

}

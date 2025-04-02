package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class TracksAdapter(

    private val tracks: List<Track>

) : RecyclerView.Adapter<TracksViewHolder>() {

    private var lastClickTime: Long = 0
    private val debounceDelay = 1000L // Задержка для последующих кликов
    private var runnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {

        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastClickTime
            lastClickTime = currentTime

            if (elapsedTime > debounceDelay) {
                // Если прошло достаточно времени, выполняем код сразу
                performClickAction(holder, position)
            } else {
                // Если клик слишком быстрый, ставим задержку
                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    performClickAction(holder, position)
                }
                handler.postDelayed(runnable!!, debounceDelay)
            }
        }

    }


    fun performClickAction(holder: TracksViewHolder, position: Int) {

        val maxHistoryItems = 10

        val sharedPreferences =
            holder.itemView.context.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(TRACK_HISTORY, null)

        val trackListType: Type?
        val trackListHistory: MutableList<Track>? //= null // Инициализируем как null

        if (!jsonString.isNullOrEmpty()) {
            //история существует
            trackListType = object : TypeToken<MutableList<Track>>() {}.type
            trackListHistory = Gson().fromJson(jsonString, trackListType)

            if (!trackListHistory.isNullOrEmpty()) {

                //история поиска существует

                if (!trackListHistory.any { it.trackId == tracks[position].trackId }) {
                    //данного трека не было в истории, значит добавляем его в историю поиска
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
                    .putString(TRACK_HISTORY, jsonTracksHistory)
                    .apply()
            }
        } else {
            //истории пока не было
            trackListHistory = mutableListOf()
            trackListHistory.add(tracks[position])
            val jsonTracksHistory: String = Gson().toJson(trackListHistory)
            sharedPreferences.edit()
                .putString(TRACK_HISTORY, jsonTracksHistory)
                .apply()
        }

        //нужно открывать AudioPlayerActivity
        val audioPlayerIntent = Intent(holder.itemView.context, AudioPlayerActivity::class.java)
        audioPlayerIntent.putExtra(CURRENT_TRACK_DATA, tracks[position])
        holder.itemView.context.startActivity(audioPlayerIntent)

    }

    override fun getItemCount() = tracks.size

}

package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.search.domain.Track

class TrackAdapter(

    private val clickListener: TrackClickListener,
    private val longClickListener: TrackClickListener


) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)

    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks.get(position))
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }

//        holder.itemView.setOnClickListener { onTrackClick(track) }
        holder.itemView.setOnLongClickListener {
            longClickListener.onTrackClick(tracks.get(position))
            true
        }

    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    override fun getItemCount() = tracks.size

}

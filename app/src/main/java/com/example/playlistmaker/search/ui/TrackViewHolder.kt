package com.example.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TracksViewBinding
import com.example.playlistmaker.dpToPx
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: TracksViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {

        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName

        try {
            binding.trackTimeValue.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(model.trackTimeMillis.toLong())
        } catch (e: Exception) {
            binding.trackTimeValue.text = model.trackTimeMillis
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .placeholder(R.drawable.place_holder)
            .into(binding.imageView)
    }
}

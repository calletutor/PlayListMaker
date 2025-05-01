package com.example.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(item: View) : RecyclerView.ViewHolder(item) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(model: Track) {

        trackName.text = model.trackName
        artistName.text = model.artistName

        try {
            trackTime.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(model.trackTimeMillis.toLong())
        } catch (e: Exception) {
            trackTime.text = model.trackTimeMillis
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .placeholder(R.drawable.place_holder)
            .into(imageView)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

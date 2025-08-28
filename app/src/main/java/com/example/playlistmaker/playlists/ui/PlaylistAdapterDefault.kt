package com.example.playlistmaker.playlists.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.playlists.domain.StringProvider
import com.example.playlistmaker.playlists.utils.WordUtils



class PlaylistAdapterDefault(private val stringProvider: StringProvider) :

    RecyclerView.Adapter<PlaylistAdapterDefault.PlaylistViewHolder>() {

    private var playlists: List<PlaylistEntity> = emptyList()
    private var onItemClickListener: ((PlaylistEntity) -> Unit)? = null

    fun submitList(newList: List<PlaylistEntity>) {
        playlists = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.playlist_view_for_playlists_fragment, parent, false)
        return PlaylistViewHolder(view, stringProvider)
    }


    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClickListener)
    }

    fun setOnItemClickListener(listener: (PlaylistEntity) -> Unit) {
        onItemClickListener = listener
    }

    class PlaylistViewHolder(
        itemView: View,
        private val stringProvider: StringProvider
    ) : RecyclerView.ViewHolder(itemView) {
        private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
        private val playlistTracksCount: TextView =
            itemView.findViewById(R.id.playlist_tracks_count)
        private val cover: ImageView = itemView.findViewById(R.id.playlist_image)

        fun bind(item: PlaylistEntity, onItemClick: ((PlaylistEntity) -> Unit)?) {

            playlistName.text = item.name
            playlistTracksCount.text =
                "${item.tracksCount} ${WordUtils.getTrackWord(item.tracksCount, stringProvider)}"


            if (!item.coverImagePath.isNullOrEmpty()) {
                cover.setImageURI(Uri.parse(item.coverImagePath))
            } else {
                cover.setImageResource(R.drawable.place_holder2_w_bg)
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}


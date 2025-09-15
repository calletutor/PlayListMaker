package com.example.playlistmaker.playlists.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewForPlaylistsFragmentBinding
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.playlists.domain.StringProvider
import com.example.playlistmaker.playlists.utils.WordUtils


class PlaylistAdapterDefault(
    private val stringProvider: StringProvider
) : RecyclerView.Adapter<PlaylistAdapterDefault.PlaylistViewHolder>() {

    private var playlists: List<PlaylistEntity> = emptyList()
    private var onItemClickListener: ((PlaylistEntity) -> Unit)? = null

    fun submitList(newList: List<PlaylistEntity>) {
        playlists = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistViewForPlaylistsFragmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaylistViewHolder(binding, stringProvider)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClickListener)
    }

    fun setOnItemClickListener(listener: (PlaylistEntity) -> Unit) {
        onItemClickListener = listener
    }

    class PlaylistViewHolder(
        private val binding: PlaylistViewForPlaylistsFragmentBinding,
        private val stringProvider: StringProvider
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistEntity, onItemClick: ((PlaylistEntity) -> Unit)?) {
            binding.playlistName.text = item.name
            binding.playlistTracksCount.text =
                "${item.tracksCount} ${WordUtils.getTrackWord(item.tracksCount, stringProvider)}"

            if (!item.coverImagePath.isNullOrEmpty()) {
                binding.playlistImage.setImageURI(Uri.parse(item.coverImagePath))
            } else {
                binding.playlistImage.setImageResource(R.drawable.place_holder2_w_bg)
            }

            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}

package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentPlaylists : Fragment() {
    private val viewModel: ViewModelPlayLists by viewModel()

    companion object {
        fun newInstance() = FragmentPlaylists()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlists, container, false)

        return view
    }
}

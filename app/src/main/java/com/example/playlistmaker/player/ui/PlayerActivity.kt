package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.domain.Creator
import com.example.playlistmaker.databinding.PlayerActivityBinding
import com.example.playlistmaker.main.ui.CURRENT_TRACK_DATA
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.playlistmaker.dpToPx

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: PlayerActivityBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        viewModel = ViewModelProvider(
            this,
            Creator.providePlayerViewModelFactory()
        )[PlayerViewModel::class.java]

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CURRENT_TRACK_DATA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(CURRENT_TRACK_DATA)
        }

        if (track != null) {
            viewModel.preparePlayer(track)
            bindTrackInfoToUI(track)
        } else {
            Log.e("PlayerActivity", "Track is null")
            finish()
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            // Кнопка Play/Pause
            binding.playImage.setImageResource(state.buttonResId)

            // Время воспроизведения
            binding.playingTime.text = state.playTime
        }
    }

    private fun setupClickListeners() {
        binding.playImage.setOnClickListener {
            viewModel.playbackControl() // кнопка play/pause
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindTrackInfoToUI(track: Track) {

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.albumValue.text = track.collectionName
        binding.yearValue.text = track.releaseDate.take(4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .placeholder(R.drawable.place_holder)
            .into(binding.imageView)
    }
}

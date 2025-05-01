package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.main.ui.CURRENT_TRACK_DATA
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel

    private lateinit var playImage: ImageView
    private lateinit var playTime: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.audio_player_activity)

        initViews()
        setupListeners()
        setupViewModel()

        val track = getTrackFromIntent()
        if (track != null) {
            bindTrackData(track)
            viewModel.preparePlayer(track)
        }
    }

    private fun initViews() {
        playImage = findViewById(R.id.playImage)
        playTime = findViewById(R.id.playTime)
        toolbar = findViewById(R.id.toolbar)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        album = findViewById(R.id.album)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        imageView = findViewById(R.id.imageView)

        playTime.text = "00:00"
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener { finish() }
        playImage.setOnClickListener { viewModel.playbackControl() }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AudioPlayerViewModel::class.java]

        viewModel.playTimeLiveData.observe(this) { time ->
            playTime.text = time
        }

        viewModel.playerButtonStateLiveData.observe(this) { drawableId ->
            playImage.setImageDrawable(ContextCompat.getDrawable(this, drawableId))
        }
    }

    private fun bindTrackData(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        album.text = track.collectionName
        country.text = track.country
        genre.text = track.primaryGenreName
        year.text = track.releaseDate.take(4)

        track.trackTimeMillis?.toLongOrNull()?.let { timeMillis ->
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
            trackTime.text = formattedTime
        }

        val tempURI = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(imageView)
            .load(tempURI)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, imageView.context)))
            .placeholder(R.drawable.place_holder)
            .into(imageView)
    }

    private fun getTrackFromIntent(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CURRENT_TRACK_DATA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(CURRENT_TRACK_DATA)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

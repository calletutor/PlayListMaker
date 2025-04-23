package com.example.playlistmaker.presentation

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.CURRENT_TRACK_DATA
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 1000L

    }

    var totalPlayTimeElapsed = 0
    private var mainThreadHandler: Handler? = null
    private var mediaPlayer = MediaPlayer()
    var currentTrack: Track? = null
    private var playerState = STATE_DEFAULT
    lateinit var playImage: ImageView
    lateinit var playTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.audio_player_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playTime = findViewById(R.id.playTime)
        playTime.setText("00:00");

        playImage = findViewById(R.id.playImage)

        mainThreadHandler = Handler(Looper.getMainLooper())
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val imageView: ImageView = findViewById(R.id.imageView)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val trackTime: TextView = findViewById(R.id.trackTime)
        val album: TextView = findViewById(R.id.album)
        val year: TextView = findViewById(R.id.year)
        val genre: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)

        playImage.setOnClickListener {
            playbackControl()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        currentTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CURRENT_TRACK_DATA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(CURRENT_TRACK_DATA)
        }

        currentTrack?.let { track ->
            preparePlayer(track)
        }

        val tempURI = currentTrack?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(imageView)
            .load(tempURI)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, imageView.context)))
            .placeholder(R.drawable.place_holder)
            .into(imageView)

        currentTrack?.let { track ->
            trackName.text = track.trackName
            artistName.text = track.artistName
            album.text = track.collectionName
            country.text = track.country
            genre.text = track.primaryGenreName
            year.text = track.releaseDate.take(4)
        }

        currentTrack?.trackTimeMillis?.toLongOrNull()?.let { timeMillis ->
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
            trackTime.text = formattedTime
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer(currentTrack: Track) {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play_button))
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_button_light))
        if (playerState != STATE_PAUSED) {
            totalPlayTimeElapsed = 0
            startTimer()
        }
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play_button))
        playerState = STATE_PAUSED
    }

    private fun startTimer() {
        mainThreadHandler?.postDelayed(createUpdateTimerTask(), DELAY)
    }


    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {

                if (playerState == STATE_PLAYING) {
                    totalPlayTimeElapsed = totalPlayTimeElapsed + 1
                }

                if (playerState == STATE_PREPARED) {
                    playTime.setText("00:00");
                    return
                }

                playTime.text = String.format(
                    "%02d:%02d",
                    totalPlayTimeElapsed / 60,
                    totalPlayTimeElapsed % 60
                )

                mainThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }
}

package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.audio_player_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val imageView: ImageView = findViewById(R.id.imageView)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val trackTime: TextView = findViewById(R.id.trackTime)
        val album: TextView = findViewById(R.id.album)
        val year: TextView = findViewById(R.id.year)
        val genre: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val currentTrackData = intent.getStringExtra("currentTrackData")
        val currentTrack: Track = Gson().fromJson(currentTrackData, Track::class.java)

        val tempURI = currentTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(imageView)
            .load(tempURI)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, imageView.context)))
            .placeholder(R.drawable.place_holder)
            .into(imageView)

        //нужно распределить содержимое currentTrack по соответствующим элементам лэйаута
        trackName.text = currentTrack.trackName
        try {
            trackTime.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(currentTrack.trackTimeMillis.toLong())
        } catch (e: Exception) {
            trackTime.text = currentTrack.trackTimeMillis // Значение по умолчанию при ошибке
        }
        artistName.text = currentTrack.artistName
        album.text = currentTrack.collectionName
        country.text = currentTrack.country
        genre.text = currentTrack.primaryGenreName
        year.text = currentTrack.releaseDate.take(4)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

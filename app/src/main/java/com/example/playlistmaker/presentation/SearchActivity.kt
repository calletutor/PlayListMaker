package com.example.playlistmaker.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.CURRENT_TRACK_DATA
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.TracksResult
import com.example.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private val getSearchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    lateinit var clearHistoryButton: Button
    lateinit var searchHistoryTitle: TextView
    lateinit var clearButton: ImageView
    lateinit var searchFailedImage: ImageView
    lateinit var refreshButton: Button
    lateinit var searchFailedTextView: TextView
    private lateinit var inputEditText: EditText
    private var currentText: String? = null

    lateinit var tracksList: RecyclerView
    lateinit var progressBar: ProgressBar
    private var isClickAllowed = true
    private var tracks: MutableList<Track> = mutableListOf()

    override fun onResume() {
        super.onResume()
        if (inputEditText.isFocused() && inputEditText.text.isEmpty()) {
            showTracksSearchHistory()
        }
    }

    private val tracksAdapter = TracksAdapter {
        if (clickDebounce()) {
            putTrackToSearchHistory(it)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(CURRENT_TRACK_DATA, it)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchAttempt() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tracksList = findViewById(R.id.recyclerView)
        tracksList.layoutManager = LinearLayoutManager(this)

        tracksList = findViewById(R.id.recyclerView)
        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = this.tracksAdapter

        progressBar = findViewById(R.id.progressBar)
        clearHistoryButton = findViewById(R.id.clearHistory)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        inputEditText = findViewById(R.id.searchInputEditText)
        clearButton = findViewById(R.id.clear_button)
        searchFailedImage = findViewById(R.id.searchFailedImage)
        refreshButton = findViewById(R.id.refreshButton)
        searchFailedTextView = findViewById(R.id.errorMessage)
        refreshButton.visibility = GONE
        searchFailedTextView.visibility = GONE

        tracksList = findViewById(R.id.recyclerView)

        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = this.tracksAdapter
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showTracksSearchHistory()
            }
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable?) {

                if (inputEditText.text.isEmpty()) {

                    handler.removeCallbacks(searchRunnable)
                    progressBar.visibility = GONE
                    clearButton.visibility = GONE

                    showTracksSearchHistory()

                } else {

                    tracks.clear()
                    this@SearchActivity.tracksAdapter.tracks = tracks
                    this@SearchActivity.tracksAdapter.notifyDataSetChanged()

                    clearHistoryButton.visibility = GONE
                    searchHistoryTitle.visibility = GONE
                    searchFailedImage.visibility = GONE
                    searchFailedTextView.visibility = GONE
                    refreshButton.visibility = GONE

                    if (s.toString().isEmpty()) {
                        clearButton.isVisible = false
                    } else {
                        clearButton.isVisible = true
                        currentText = inputEditText.text.toString()
                    }

                    searchDebounce()

                }
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {

            ClearTracksSearchHistory()

        }

        refreshButton.setOnClickListener {

            searchFailedImage.visibility = GONE
            searchFailedTextView.visibility = GONE
            refreshButton.visibility = GONE

            if (inputEditText.text.isNotEmpty()) {
                searchAttempt()
            }
        }

        clearButton.setOnClickListener {
            this.tracksAdapter.notifyDataSetChanged()
            searchFailedTextView.visibility = GONE
            searchFailedImage.visibility = GONE
            refreshButton.visibility = GONE
            inputEditText.setText("")
            clearButton.visibility = GONE
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            handler.removeCallbacks(searchRunnable)
            progressBar.visibility = GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun searchAttempt() {

        progressBar.visibility = View.VISIBLE

        Creator.provideTracksInteractor()

            .searchTracks(inputEditText.text.toString(), object : TracksInteractor.TracksConsumer {

                override fun consume(result: TracksResult) {
                    runOnUiThread {
                        progressBar.visibility = GONE

                        when {
                            result.isSuccess && result.tracks.isNotEmpty() -> {
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                this@SearchActivity.tracksAdapter.notifyDataSetChanged()
                            }

                            result.isSuccess -> {
                                searchFailedTextView.text = "Ничего не найдено."
                                searchFailedImage.setImageResource(R.drawable.ic_not_found)
                                searchFailedImage.visibility = View.VISIBLE
                                searchFailedTextView.visibility = View.VISIBLE
                            }

                            result.isNetworkError -> {
                                searchFailedTextView.text = "Проблема с сетью."
                                searchFailedImage.setImageResource(R.drawable.ic_no_connecton)
                                searchFailedImage.visibility = View.VISIBLE
                                searchFailedTextView.visibility = View.VISIBLE
                                refreshButton.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })
    }

    fun ClearTracksSearchHistory() {
        getSearchHistoryInteractor.clearTrackListOfHistory()
        tracks.clear()
        tracksAdapter.tracks = tracks
        tracksAdapter.notifyDataSetChanged()
        searchHistoryTitle.visibility = GONE
        clearHistoryButton.visibility = GONE
    }

    fun showTracksSearchHistory() {
        Creator.provideSearchHistoryInteractor().getSavedHistory(object :
            SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(trackList: List<Track>) {
                if (trackList.isNotEmpty()) {
                    this@SearchActivity.tracksAdapter.tracks = trackList.toMutableList()
                    this@SearchActivity.tracksAdapter.notifyDataSetChanged()
                    tracksList.adapter = this@SearchActivity.tracksAdapter
                    searchHistoryTitle.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(EDIT_TEXT)
        inputEditText.setText(currentText)
    }

    private fun putTrackToSearchHistory(track: Track) {
        getSearchHistoryInteractor.addTrackToHistory(track)
    }
}

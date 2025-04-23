package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

private const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {

    lateinit var trackListType: Type
    var jsonString: String? = null
    lateinit var sharedPreferences: SharedPreferences
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(ItunesApi::class.java)

    var trackList: MutableList<Track> = mutableListOf()
    var tracksAdapter = TracksAdapter(trackList)
    lateinit var clearHistoryButton: Button
    lateinit var searchHistoryTitle: TextView
    lateinit var clearButton: ImageView
    lateinit var searchFailedImage: ImageView
    lateinit var refreshButton: Button
    lateinit var searchFailedTextView: TextView
    private lateinit var inputEditText: EditText
    private var currentText: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
    }

    override fun onResume() {

        super.onResume()

        if (inputEditText.isFocused() && inputEditText.text.isEmpty()) {
            showTrackListHistory()
        }
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

        progressBar = findViewById(R.id.progressBar)
        clearHistoryButton = findViewById(R.id.clearHistory)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        inputEditText = findViewById(R.id.searchInputEditText)
        clearButton = findViewById(R.id.clear_button)
        searchFailedImage = findViewById(R.id.searchFailedImage)
        refreshButton = findViewById(R.id.refreshButton)
        searchFailedTextView = findViewById(R.id.errorMessage)
        refreshButton.visibility = View.GONE
        searchFailedTextView.visibility = View.GONE

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tracksAdapter = TracksAdapter(trackList)
        recyclerView.adapter = tracksAdapter

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showTrackListHistory()
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

                    //поле пустое
                    handler.removeCallbacks(searchRunnable) //критическое замечание
                    progressBar.visibility = View.GONE //критическое замечание

                    clearButton.visibility = View.GONE

                    sharedPreferences = getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
                    jsonString = sharedPreferences.getString(TRACK_HISTORY, null)

                    if (!jsonString.isNullOrEmpty()) {
                        trackListType = object : TypeToken<MutableList<Track>>() {}.type
                        trackList = Gson().fromJson(jsonString, trackListType)
                        tracksAdapter = TracksAdapter(trackList)
                        recyclerView.adapter = tracksAdapter
                        clearHistoryButton.visibility = View.VISIBLE
                        searchHistoryTitle.visibility = View.VISIBLE
                    }

                } else {

                    //поле не пустое

                    trackList.clear()
                    tracksAdapter = TracksAdapter(trackList)
                    recyclerView.adapter = tracksAdapter
                    clearHistoryButton.visibility = View.GONE
                    searchHistoryTitle.visibility = View.GONE
                    searchFailedImage.visibility = View.GONE
                    searchFailedTextView.visibility = View.GONE
                    refreshButton.visibility = View.GONE

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
            trackList.clear()
            tracksAdapter = TracksAdapter(trackList)
            recyclerView.adapter = tracksAdapter

            sharedPreferences.edit()
                .clear()
                .apply()

            clearHistoryButton.visibility = View.GONE
            searchHistoryTitle.visibility = View.GONE

        }

        refreshButton.setOnClickListener {

            searchFailedImage.visibility = View.GONE
            searchFailedTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE

            if (inputEditText.text.isNotEmpty()) {

                searchAttempt()

            }
        }

        clearButton.setOnClickListener {
            trackList.clear()
            tracksAdapter.notifyDataSetChanged()
            searchFailedTextView.visibility = View.GONE
            searchFailedImage.visibility = View.GONE
            refreshButton.visibility = View.GONE//здесь был баг
            inputEditText.setText("")
            clearButton.visibility = View.GONE
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            handler.removeCallbacks(searchRunnable)//критическое замечание
            progressBar.visibility = View.GONE//критическое замечание
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun searchAttempt() {

        progressBar.visibility = View.VISIBLE

        imdbService.search(inputEditText.text.toString()).enqueue(object :
            Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {

                //ответ от сервера получен
                progressBar.visibility = View.GONE

                if (!inputEditText.text.isNullOrEmpty()) {//критическое замечание

                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {

                            trackList.addAll(response.body()?.results!!)
                            tracksAdapter = TracksAdapter(trackList)
                            recyclerView.adapter = tracksAdapter

                        }

                        if (trackList.isEmpty()) {
                            searchFailedTextView.setText(R.string.searchFailed)
                            searchFailedImage.setImageResource(R.drawable.ic_not_found)
                            searchFailedImage.visibility = View.VISIBLE
                            searchFailedTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {

                progressBar.visibility = View.GONE

                searchFailedTextView.setText(R.string.connectionFaied)
                searchFailedImage.setImageResource(R.drawable.ic_no_connecton)
                searchFailedImage.visibility = View.VISIBLE
                searchFailedTextView.visibility = View.VISIBLE
                refreshButton.visibility = View.VISIBLE
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

    fun showTrackListHistory() {

        sharedPreferences = getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        jsonString = sharedPreferences.getString(TRACK_HISTORY, null)

        if (!jsonString.isNullOrEmpty()) {

            trackListType = object : TypeToken<MutableList<Track>>() {}.type
            trackList = Gson().fromJson(jsonString, trackListType)

            tracksAdapter = TracksAdapter(trackList)
            recyclerView.adapter = tracksAdapter

            clearHistoryButton.visibility = View.VISIBLE
            searchHistoryTitle.visibility = View.VISIBLE

        }
    }
}

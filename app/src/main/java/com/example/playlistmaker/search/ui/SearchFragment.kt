package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.SearchScreenState
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

object SerarchConstants {
    const val TIME_FORMAT = "mm:ss"
}

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val clickRunnable = Runnable { isClickAllowed = true }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                val uiState by viewModel.uiState.observeAsState(SearchScreenState())
                var query by remember { mutableStateOf(viewModel.currentQuery) }

                SearchScreen(
                    uiState = uiState,
                    currentQuery = query,
                    onQueryChanged = { q ->
                        query = q
                        viewModel.currentQuery = q

                        if (q.isNotEmpty()) {
                            viewModel.hideHistory()
                            viewModel.searchDebounce(q)
                        } else {
                            viewModel.cancelSearchDebounce()
                            lifecycleScope.launch { viewModel.loadSearchHistory() }
                        }
                    },
                    onFocusGained = { lifecycleScope.launch { viewModel.loadSearchHistory() } },
                    onClearQuery = {
                        query = ""
                        viewModel.currentQuery = ""
                        viewModel.cancelSearchDebounce()
                        viewModel.clearTracks()
                        lifecycleScope.launch { viewModel.loadSearchHistory() }
                    },
                    onRetry = { viewModel.retrySearch() },
                    onTrackClick = { track ->
                        if (clickDebounce()) {
                            viewModel.saveTrackToHistory(track)
                            openPlayerScreen(track)
                        }
                    },
                    onTrackLongClick = { track ->
                        //do nothing
                    },
                    onClearHistory = {
                        viewModel.clearTracks()
                        viewModel.clearHistory()
                    }
                )
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val allowed = isClickAllowed
        if (allowed) {
            isClickAllowed = false
            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)
        }
        return allowed
    }

    private fun openPlayerScreen(track: Track) {
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(clickRunnable)
    }
}

@Composable
fun SearchScreen(
    uiState: SearchScreenState,
    currentQuery: String?,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
    onFocusGained: () -> Unit
) {

    var isFocused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf(currentQuery.orEmpty()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dual_color4))
    )
    {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onQueryChanged(it)
                    },

                    shape = RoundedCornerShape(16.dp),

                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.search),
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = Color.Gray
                        )
                    },

                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                query = ""
                                onClearQuery()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clear),
                                    contentDescription = stringResource(id = R.string.clear_icon),
                                    tint = Color.Gray
                                )
                            }
                        }
                    },

                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search),
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                    }, textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .width(250.dp)
                        .padding(0.dp)

                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && !isFocused) {
                                isFocused = true
                                if (query.isEmpty()) {
                                    scope.launch {
                                        onFocusGained()

                                    }
                                }

                            } else if (!focusState.isFocused && isFocused) {
                                //do nothing
                            }
                        },

                    colors = TextFieldDefaults.textFieldColors(

                        textColor = colorResource(id = R.color.black),
                        backgroundColor = colorResource(id = R.color.search_edit_text),

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = colorResource(id = R.color.blue)
                    )

                )

            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(100.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(44.dp),
                    color = Color.Blue,
                )
            }

            uiState.error?.let { error ->
                when (error) {
                    is SearchError.NothingFound -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 160.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_not_found),
                                contentDescription = stringResource(id = R.string.nothing_found),
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.nothing_found),
                                color = colorResource(id = R.color.dual_color),
                                fontSize = 20.sp
                            )
                        }
                    }

                    is SearchError.Network -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(160.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_no_connecton),
                            contentDescription = stringResource(id = R.string.connection_failed),
                            modifier = Modifier
                                .size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            fontSize = 20.sp,
                            text = stringResource(id = R.string.connection_failed),
                            color = colorResource(id = R.color.dual_color)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onRetry,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(id = R.color.dual_color)
                            )
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.dual_color4),
                                text = stringResource(id = R.string.refresh)
                            )

                        }


                    }

                    SearchError.Unknown -> TODO()

                }
            }

            if (uiState.isShowingHistory) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.searchHistory),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    fontSize = 19.sp,
                    color = colorResource(id = R.color.dual_color)

                )
            }


            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()

            )


            {
                items(uiState.tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                        onLongClick = { onTrackLongClick(track) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }

            }
        }

        if (uiState.isShowingHistory) {
            Button(
                onClick = { onClearHistory() },
                colors = ButtonDefaults.buttonColors(

                    backgroundColor = colorResource(id = R.color.dual_color),
                    contentColor = colorResource(id = R.color.dual_color4)

                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = stringResource(R.string.clearSearchHistory))
            }
        }
    }
}


@Composable
fun TrackItem(track: Track, onClick: () -> Unit, onLongClick: () -> Unit) {

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(

            containerColor = colorResource(id = R.color.dual_color4)

        )
    )

    {

        Row(
            modifier = Modifier
                .padding(start = 12.dp)
                .padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = stringResource(id = R.string.album_cover),
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp)),
                error = painterResource(id = R.drawable.place_holder),
                placeholder = painterResource(id = R.drawable.place_holder2)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                androidx.compose.material3.Text(
                    text = track.trackName,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.dual_color3),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row {
                    androidx.compose.material3.Text(
                        text = track.artistName,
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.dual_color2),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.ellipse),
                        contentDescription = stringResource(id = R.string.separator),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(horizontal = 8.dp),
                        tint = colorResource(id = R.color.dual_color2)
                    )

                    androidx.compose.material3.Text(
                        text = SimpleDateFormat(
                            SerarchConstants.TIME_FORMAT,
                            Locale.getDefault()
                        ).format(track.trackTimeMillis.toLong()),
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.dual_color2),
                        style = MaterialTheme.typography.bodyMedium
                    )


                }
            }

            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = stringResource(R.string.action_icon),
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp),
                tint = colorResource(id = R.color.dual_color2)
            )
        }
    }
}

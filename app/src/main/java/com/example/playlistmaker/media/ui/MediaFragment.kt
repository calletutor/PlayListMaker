package com.example.playlistmaker.media.ui

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.playlists.ui.NewPlaylistViewModel
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.Card
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


object MediaConstants {
    const val TAB_COUNT = 2
    const val DEFAULT_PLAYLIST_ID = -1L
    const val TIME_FORMAT = "mm:ss"
}

class MediaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = NavHostFragment.findNavController(this)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()

                            .background(colorResource(id = R.color.dual_color4))
                    ) {
                        MediaScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MediaScreen(navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { MediaConstants.TAB_COUNT })
    val tabs = listOf(stringResource(R.string.selected_tracks), stringResource(R.string.playlists))
    val coroutineScope = rememberCoroutineScope()

    Column {

        Text(
            text = stringResource(id = R.string.media),
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.dual_color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Start
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = colorResource(id = R.color.dual_color4),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = colorResource(id = R.color.dual_color)
                )
            }, divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.dual_color)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> SelectedTracksScreen { track ->
                    navController.navigate(
                        MediaFragmentDirections.actionMediaFragmentToPlayerFragment(
                            track
                        )
                    )
                }

                1 -> PlaylistsScreen(
                    onCreateNewPlaylist = {
                        navController.navigate(
                            MediaFragmentDirections.actionMediaFragmentToNewPlaylistFragment(
                                MediaConstants.DEFAULT_PLAYLIST_ID
//                                -1
                            )
                        )
                    },
                    onPlaylistClick = { playlistId ->
                        navController.navigate(
                            MediaFragmentDirections.actionMediaFragmentToPlaylistContentFragment(
                                playlistId.toLong()
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SelectedTracksScreen(
    viewModel: SelectedTracksViewModel = getViewModel(),
    onTrackClick: (Track) -> Unit
) {
    val selectedTrackList by viewModel.favoriteTracks.observeAsState(emptyList())

    if (selectedTrackList.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_not_found),
                contentDescription = stringResource(id = R.string.media_library_is_empty),
                modifier = Modifier.size(120.dp)
            )

            Text(fontSize = 20.sp, text = stringResource(id = R.string.media_library_is_empty))
        }

    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(selectedTrackList) { track ->
                SelectedTrackItem(track) {
                    onTrackClick(track)
                }
            }
        }
    }
}

@Composable
fun SelectedTrackItem(track: Track, onClick: () -> Unit) {

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(end = 10.dp),
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
                            MediaConstants.TIME_FORMAT,
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
                contentDescription = stringResource(id = R.string.action_icon),
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp),
                tint = colorResource(id = R.color.dual_color2)
            )
        }
    }
}

@Composable
fun PlaylistsScreen(
    viewModel: NewPlaylistViewModel = getViewModel(),
    onCreateNewPlaylist: () -> Unit,
    onPlaylistClick: (Int) -> Unit
) {
    val playlists by viewModel.playlists.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Button(
                onClick = onCreateNewPlaylist,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.dual_color)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.new_playlist),
                    fontSize = 14.sp,
                    color = colorResource(R.color.dual_color4)
                )
            }
        }

        if (playlists.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_not_found),
                    contentDescription = stringResource(id = R.string.no_playlist_created),
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material.Text(
                    stringResource(id = R.string.no_playlist_created),
                    color = colorResource(id = R.color.dual_color),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.playlistId.toInt()) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistEntity, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(

            containerColor = colorResource(id = R.color.dual_color4)

        )
    ) {

        AsyncImage(
            model = playlist.coverImagePath,
            contentDescription = stringResource(id = R.string.album_cover),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(8.dp)),
            error = painterResource(id = R.drawable.place_holder),
            placeholder = painterResource(id = R.drawable.place_holder2)
        )

        Text(
            text = playlist.name,
            fontSize = 12.sp,
            color = colorResource(id = R.color.dual_color),
            modifier = Modifier

                .padding(start = 16.dp)
        )



        Text(
            text = playlist.tracksCount.toString(),
            fontSize = 12.sp,
            color = colorResource(id = R.color.dual_color),
            modifier = Modifier
                .padding(start = 16.dp)
        )
    }
}

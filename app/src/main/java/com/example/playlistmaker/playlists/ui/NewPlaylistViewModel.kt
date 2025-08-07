package com.example.playlistmaker.playlists.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.favorites.data.db.PlaylistEntity
import com.example.playlistmaker.playlists.domain.ImageStorageManager
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.playlists.domain.SavePlaylistUseCase
import com.example.playlistmaker.playlists.domain.StringProvider
import kotlinx.coroutines.launch


class NewPlaylistViewModel(

    private val savePlaylistUseCase: SavePlaylistUseCase,
    private val playlistRepository: PlaylistRepository,
    private val imageStorageManager: ImageStorageManager,
    private val stringProvider: StringProvider

) : ViewModel() {

    private var currentImagePath: String? = null
    private var currentTrackIds: List<Int> = emptyList()
    private val _loadedPlaylist = MutableLiveData<PlaylistEntity?>()
    val loadedPlaylist: LiveData<PlaylistEntity?> = _loadedPlaylist
    val playlistName = MutableLiveData("")

    val isCreateButtonEnabled: LiveData<Boolean> = playlistName.map {
        it.isNotBlank()
    }

    val playlists: LiveData<List<PlaylistEntity>> =
        playlistRepository.getAllPlaylists().asLiveData()

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> get() = _errorEvent

    private val _successEvent = MutableLiveData<String?>()
    val successEvent: LiveData<String?> get() = _successEvent

    fun onSuccessHandled() {
        _successEvent.value = null
    }

    fun onErrorHandled() {
        _errorEvent.value = null
    }

    fun savePlaylist(
        name: String,
        description: String?,
        imageUri: Uri? = null,
        trackIds: List<Int>? = null,
        id: Long? = null
    ) {
        viewModelScope.launch {
            if (id == null) {
                val existing = playlistRepository.getPlaylistByName(name)
                if (existing != null) {
                    _errorEvent.postValue(
                        stringProvider.getString(
                            R.string.error_playlist_exists,
                            name
                        )
                    )
                    return@launch
                }
            }

            val imagePath = imageUri?.let {
                imageStorageManager.copyImageToInternalStorage(it)
            } ?: currentImagePath // если пользователь не выбрал новую картинку — используем старую

            val finalTrackIds = trackIds ?: currentTrackIds

            val playlist = PlaylistEntity(
                playlistId = id ?: 0,
                name = name,
                description = description,
                coverImagePath = imagePath,
                tracksCount = finalTrackIds.size,
                createdAt = System.currentTimeMillis()
            )

            savePlaylistUseCase(playlist)
            _successEvent.postValue(
                stringProvider.getString(
                    R.string.playlist_created_successfully,
                    name
                )
            )
        }
    }








//
//
//
//    fun savePlaylist(
//        name: String,
//        description: String?,
//        imageUri: Uri? = null,
//        trackIds: List<Int> = emptyList()
//    ) {
//        viewModelScope.launch {
//            val existing = playlistRepository.getPlaylistByName(name)
//            if (existing != null) {
//                _errorEvent.postValue(
//                    stringProvider.getString(
//                        R.string.error_playlist_exists,
//                        name
//                    )
//                )
//                return@launch
//            }
//
//            val imagePath = imageUri?.let { imageStorageManager.copyImageToInternalStorage(it) }
//
//            val playlist = PlaylistEntity(
//                name = name,
//                description = description,
//                coverImagePath = imagePath,
//                tracksCount = trackIds.size,
//                createdAt = System.currentTimeMillis()
//            )
//            savePlaylistUseCase(playlist)
//
//            _successEvent.postValue(
//                stringProvider.getString(
//                    R.string.playlist_created_successfully,
//                    name
//                )
//            )
//        }
//    }
//
//
//
//
//
//




    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val playlist = playlistRepository.getPlaylistById(id)
            _loadedPlaylist.postValue(playlist)
            playlist?.let {
                playlistName.postValue(it.name)
                currentImagePath = it.coverImagePath // сохраняем текущую обложку

                // Получаем текущие треки этого плейлиста
                val tracks = playlistRepository.getTracksForPlaylist(it)
                currentTrackIds = tracks.mapNotNull { trackEntity ->
                    trackEntity.trackId
//                    trackEntity.trackId?.toIntOrNull() // зависит от типа trackId
                }
            }
        }
    }

}

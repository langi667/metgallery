package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepository
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepositoryEntry
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.Defines
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ObjectDetailViewModel : ViewModel() {

    sealed class State {
        object Loading : State()

        class LoadedWithSuccess(val metObjectUIRepresentable: METObjectUIRepresentable) : State()
        class LoadedWithError(val error: Throwable, val objectID: Int) : State()
    }

    val images = mutableStateListOf<ImageRepositoryEntry>()
    val selectedImage = mutableStateOf<ImageRepositoryEntry?>(null)

    private val _state = MutableStateFlow<State>(State.Loading)
    private val objectID = MutableStateFlow(Defines.InvalidID)

    val state = objectID
        .debounce(500)
        .map { newID ->
            if (newID == Defines.InvalidID) {
                return@map State.Loading
            }

            repository.fetch(newID)

            val newState: State
            val latest = repository.latest
            val metObject = latest.result?.getOrNull()

            newState = if (metObject != null) {
                State.LoadedWithSuccess(
                    METObjectUIRepresentable(
                        metObject = metObject,
                        createEntriesImmediately = true
                    )
                )
            } else {
                val error = latest.error ?: NetworkError.InvalidState
                State.LoadedWithError(error, newID)
            }

            newState
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private val repository = METObjectsRepository()
    private val imageRepository = ImageRepository() // TODO: check if maybe object makes sense

    private val metObjectUIRepresentable: METObjectUIRepresentable?
        get() {
            (state.value as? State.LoadedWithSuccess)?.let { state ->
                return state.metObjectUIRepresentable
            }

            return null
        }

    init {
        viewModelScope.launch {
            launch {
                state.collect { _ ->
                    handleStateChanged()
                }
            }
        }
    }

    fun loadObjectForID(objectID: Int) {
        this.objectID.value = objectID
    }

    fun onImageSelected(imageData: ImageRepositoryEntry?) {
        if (imageData == null) {
            return
        }

        selectedImage.value = imageData
    }

    fun onBackPressed(navController: NavController) {
        if (selectedImage.value != null) {
            selectedImage.value = null
        } else {
            // TODO: cancel, clear
            navController.popBackStack()
        }
    }

    fun deselectImage() {
        selectedImage.value = null
    }

    private suspend fun loadImages() {
        val imageData = metObjectUIRepresentable?.metObject?.imageData ?: return

        imageData.forEach { currImageData ->
            viewModelScope.launch {
                val url = currImageData.smallImageURL ?: currImageData.imageURL
                val imageResult = imageRepository.fetchImage(url)

                handleImageLoaded(url, imageResult)
            }
        }
    }

    private fun handleImageLoaded(url: String, imageResult: Result<Bitmap>) =
        MainScope().launch(Dispatchers.Main) {
            val imageEntry =
                ImageRepositoryEntry(url, imageResult) // TODO: refactor, should use own model
            images.add(imageEntry)
        }

    private fun handleStateChanged() {
        if (state.value is State.LoadedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }
}
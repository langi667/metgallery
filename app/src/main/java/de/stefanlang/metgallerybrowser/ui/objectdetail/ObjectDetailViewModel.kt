package de.stefanlang.metgallerybrowser.ui.objectdetail

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepository
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepositoryEntry
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.Defines
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ObjectDetailViewModel : ViewModel() {

    sealed class State {
        object Loading : State()

        class LoadedWithSuccess(val metObjectUIRepresentable: METObjectUIRepresentable) : State()
        class LoadedWithError(val objectID: Int) : State()
    }

    val images = mutableStateListOf<ImageLoadResult>()
    val selectedImage = mutableStateOf<ImageLoadResult?>(null)

    private val _state = MutableStateFlow<State>(State.Loading)
    private val objectID = MutableStateFlow(Defines.InvalidID)

    val state = objectID
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
                State.LoadedWithError(newID)
            }

            newState
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private val repository = METObjectsRepository()
    private val imageRepository = ImageRepository()

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

    fun onImageSelected(imageData: ImageLoadResult?) {
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
                imageRepository.fetch(url)
                val imageResult = imageRepository.entryForQuery(url)

                handleImageLoaded(imageResult)
            }
        }
    }

    private fun handleImageLoaded(entry: ImageRepositoryEntry?) {
        entry ?: return

        MainScope().launch(Dispatchers.Main) {
            val result = imageLoadResultFromEntry(entry)
            images.add(result)
        }
    }

    private fun handleStateChanged() {
        if (state.value is State.LoadedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }

    private fun imageLoadResultFromEntry(entry: ImageRepositoryEntry): ImageLoadResult {
        val image = entry.resultValue
        val retVal: ImageLoadResult = if( image != null){
            ImageLoadResult.Success(entry.query ?: "", image)
        }
        else {
            ImageLoadResult.Failure(entry.query ?: "", entry.error ?: NetworkError.InvalidState)
        }

        return retVal
    }
}
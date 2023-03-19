package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.metgallerybrowser.models.ImageData
import de.stefanlang.metgallerybrowser.models.ImageLoadResult
import de.stefanlang.metgallerybrowser.objectdetail.model.METObject
import de.stefanlang.metgallerybrowser.objectdetail.repository.METObjectRepositoryImpl
import de.stefanlang.metgallerybrowser.repository.image.ImageRepository
import de.stefanlang.metgallerybrowser.utils.METObjectEntryBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectDetailViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val objectsRepository: METObjectRepositoryImpl,
    private val metObjectEntryBuilder: METObjectEntryBuilder
) : ViewModel() {

    sealed class State {
        object Loading : State()
        object NotFound : State()
        class LoadedWithSuccess(val metObject: METObject) : State()
        object LoadedWithError : State()
    }

    val images = mutableStateListOf<ImageLoadResult>()
    val selectedImage = mutableStateOf<ImageLoadResult?>(null)
    val title: String?
        get() = metObject?.title

    val entries: List<METObjectEntryBuilder.Entry>
        get() {
            val metObject = this.metObject ?: return emptyList()
            val retVal = metObjectEntryBuilder.buildFor(metObject)

            return retVal
        }

    val imageData: List<ImageData>
        get() {
            val metObject = this.metObject ?: return emptyList()
            val retVal = metObject.imageData

            return retVal
        }

    private val _state = MutableStateFlow<State>(State.Loading)
    private val objectID = MutableStateFlow<Int?>(null)

    val state = objectID
        .map { newID ->
            if (newID == null) {
                return@map State.Loading
            }

            val latest = objectsRepository.fetchObjectForResult(newID)
            stateForResult(latest)
        }
        // TODO: add onEach and add clear ?? ?
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private val metObject: METObject?
        get() {
            val currState: State = state.value
            val retVal = if (currState is State.LoadedWithSuccess) {
                currState.metObject
            } else {
                null
            }

            return retVal
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

    fun allLoadedImages(): List<ImageLoadResult.Success> {
        val retVal = this.images.mapNotNull { currImageLoadResult ->
            if (currImageLoadResult is ImageLoadResult.Success) {
                currImageLoadResult
            } else {
                null
            }
        }

        return retVal
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
        val imageData = metObject?.imageData ?: return

        imageData.forEach { currImageData ->
            viewModelScope.launch {
                val url = currImageData.smallImageURL ?: currImageData.imageURL
                val imageResult = imageRepository.fetchImage(url)

                handleImageLoaded(url, imageResult)
            }
        }
    }

    private fun handleImageLoaded(url: String, result: Result<Bitmap>?) {
        result ?: return

        MainScope().launch {
            val imageLoadResult = imageLoadResultForResult(url, result)
            images.add(imageLoadResult)
        }
    }

    private fun handleStateChanged() {
        if (state.value is State.LoadedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }

    private fun stateForResult(result: Result<METObject>?): State {
        result ?: return State.Loading

        val metObject = result.getOrNull()
        val error = result.exceptionOrNull()

        val retVal = if (metObject != null) {
            State.LoadedWithSuccess(metObject)
        } else if (error != null && error != NetworkError.NotFound) {
            State.LoadedWithError
        } else {
            State.NotFound
        }

        return retVal
    }

    private fun imageLoadResultForResult(url: String, result: Result<Bitmap>): ImageLoadResult {
        val image = result.getOrNull()
        val error = result.exceptionOrNull()
        val retVal: ImageLoadResult = if (image != null) {
            ImageLoadResult.Success(url, image)
        } else {
            ImageLoadResult.Failure(url, error ?: NetworkError.Unknown)
        }

        return retVal
    }
}
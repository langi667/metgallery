package de.stefanlang.feature.objectdetail.ui

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stefanlang.core.domain.image.ImageRepository
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.feature.objectdetail.model.*
import de.stefanlang.feature.objectdetail.repository.METObjectRepositoryImpl
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

    sealed class UIState {
        object Loading : UIState()
        object NotFound : UIState()
        class LoadedWithSuccess(val metObject: METObject) : UIState()
        object LoadedWithError : UIState()
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

    private val _uistate = MutableStateFlow<UIState>(UIState.Loading)
    private val objectID = MutableStateFlow<Int?>(null)

    val uiState = objectID
        .map { newID ->
            if (newID == null) {
                return@map UIState.Loading
            }

            val latest = objectsRepository.fetchObjectForResult(newID)
            stateForResult(latest)
        }

        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _uistate.value
        )

    private val metObject: METObject?
        get() {
            val currState: UIState = uiState.value
            val retVal = if (currState is UIState.LoadedWithSuccess) {
                currState.metObject
            } else {
                null
            }

            return retVal
        }

    init {
        viewModelScope.launch {
            launch {
                uiState.collect { _ ->
                    handleStateChanged()
                }
            }
        }
    }

    fun loadObjectForID(objectID: Int) {
        this.objectID.value = objectID
    }

    fun allLoadedImages(): List<ImageLoadResult.Success> {
        val retVal = mutableListOf<ImageLoadResult.Success>()

        this.imageData.forEach { currData ->
            this.images.loadedImagesForImageData(currData)?.let { loadedImage ->
                retVal.add(loadedImage)
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
        if (uiState.value is UIState.LoadedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }

    private fun stateForResult(result: Result<METObject>?): UIState {
        result ?: return UIState.Loading

        val metObject = result.getOrNull()
        val error = result.exceptionOrNull()

        val retVal = if (metObject != null) {
            UIState.LoadedWithSuccess(metObject)
        } else if (error != null && error != NetworkError.NotFound) {
            UIState.LoadedWithError
        } else {
            UIState.NotFound
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
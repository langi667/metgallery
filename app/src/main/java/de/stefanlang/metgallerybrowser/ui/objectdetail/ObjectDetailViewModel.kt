package de.stefanlang.metgallerybrowser.ui.objectdetail

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepository
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepositoryEntry
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ObjectDetailViewModel : ViewModel() {

    sealed class State {
        object Loading : State()

        class LoadedWithSuccess(val metObjectUIRepresentable: METObjectUIRepresentable) : State()
        class LoadedWithError(val error: Throwable, val objectID: Int) : State()
    }

    val images = mutableStateListOf<ImageRepositoryEntry>()

    private val _state = MutableStateFlow<State>(State.Loading)
    private val objectID = MutableStateFlow(0) // TODO: Invalid id

    val state = objectID
        .map { newID ->
            repository.fetch(newID)

            val newState: State
            val latest = repository.latest.value
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

    private val metObject: METObjectUIRepresentable?
        get(){
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

            // TODO: cancel !
            launch {
                imageRepository.latest.collect { imageData ->
                    images.add(imageData)
                }
            }
        }
    }

    fun loadObjectForID(objectID: Int) {
        this.objectID.value = objectID
    }

    private suspend fun loadImages() {
        metObject?.metObject?.imageData?.forEach {
            imageRepository.fetch(it.imageURL)
        }
    }

    private fun handleStateChanged() {
        if (state.value is State.LoadedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }
}
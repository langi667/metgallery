package de.stefanlang.metgallerybrowser.ui.objectdetail

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepository
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjectDetailViewModel : ViewModel() {

    sealed class State {
        object Loading : State()

        class FinishedWithSuccess(val metObjectUIRepresentable: METObjectUIRepresentable) : State()
        class FinishedWithError(val error: Throwable, val objectID: Int) : State()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    private val objectID = MutableStateFlow(0) // TODO: Invalid id

    val state = objectID
        .map { newID ->
            repository.fetch(newID)

            val newState: State
            val latest = repository.latest.value
            val metObject = latest.result?.getOrNull()

            newState = if (metObject != null) {
                State.FinishedWithSuccess(
                    METObjectUIRepresentable(
                        metObject = metObject,
                        createEntriesImmediately = true
                    )
                )
            } else {
                val error = latest.error ?: NetworkError.InvalidState
                State.FinishedWithError(error, newID)
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
            (state.value as? State.FinishedWithSuccess)?.let { state ->
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
                imageRepository.latest.collect { _ ->
                    print("")
                }
            }
        }
    }

    fun loadObjectForID(objectID: Int) {
        this.objectID.value = objectID
    }

    suspend fun loadImages() {
        metObject?.metObject?.imageData?.forEach {
            imageRepository.fetch(it.imageURL)
        }
    }

    private fun handleStateChanged() {
        if (state.value is State.FinishedWithSuccess) {
            viewModelScope.launch {
                loadImages()
            }
        }
    }
}
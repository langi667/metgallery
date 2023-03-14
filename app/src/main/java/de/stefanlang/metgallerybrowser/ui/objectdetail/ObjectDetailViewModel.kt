package de.stefanlang.metgallerybrowser.ui.objectdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    fun loadObjectForID(objectID: Int) {
        this.objectID.value = objectID
    }
}
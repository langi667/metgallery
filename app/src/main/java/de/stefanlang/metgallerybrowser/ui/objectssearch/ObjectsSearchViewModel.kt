package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.repositories.ObjectsSearchRepository
import kotlinx.coroutines.flow.*

class ObjectsSearchViewModel : ViewModel() {

    // region Types

    sealed class State {

        object Idle : State()

        object Loading : State()

        class FinishedWithSuccess(val objectsSearch: ObjectsSearch) : State() {
            val hasSearchResults: Boolean
                get() {
                    val retVal = objectsSearch.result?.objectIDs?.isEmpty() ?: false
                    return retVal
                }
        }

        class FinishedWithError(error: Throwable) : State()
    }

    // endregion

    // region Properties

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Idle)
    val state = searchQuery
        .debounce(1000)
        .distinctUntilChanged()
        .map { query ->
            if (query.isEmpty()) {
                return@map _state.value
            }

            val result = ObjectsSearchRepository.search(searchQuery.value)
            val objectsSearchResult = result.getOrElse { error ->
                return@map State.FinishedWithError(error)
            }

            val retVal = State.FinishedWithSuccess(objectsSearchResult)
            return@map retVal

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    // endregion

    // region Public API

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // endregion

}
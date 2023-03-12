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

        class Loading(val query: String) : State()

        class FinishedWithSuccess(val objectsSearch: ObjectsSearch) : State() {
            val hasSearchResults: Boolean
                get() {
                    val isEmpty = objectsSearch.result?.objectIDs?.isEmpty() ?: true
                    val retVal = !isEmpty
                    return retVal
                }

            val query: String
                get() = objectsSearch.query
        }

        class FinishedWithError(val error: Throwable, val query: String) : State()
    }

    // endregion

    // region Properties

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Idle)
    private val _isSearching = MutableStateFlow(false)

    val state = searchQuery
        .debounce(1000)
        .distinctUntilChanged()
        .combine(_isSearching) { query, isSearching ->
            if (query.isEmpty()) {
                return@combine State.Idle
            }

            if (!isSearching && shouldStartSearching(query)) {
                _isSearching.value = true
                return@combine State.Loading(query)
            }

            if (!isSearching) {
                return@combine _prevState
            }

            val result = repository.search(searchQuery.value)
            val retVal: State
            val value = result.getOrNull()

            retVal = if (value != null) {
                State.FinishedWithSuccess(value)
            } else {
                State.FinishedWithError(result.exceptionOrNull()!!, query)
            }

            _prevState = retVal
            return@combine retVal

        }
        .onEach { newState ->
            _isSearching.update {
                newState is State.Loading
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private var _prevState = _state.value
    private val repository = ObjectsSearchRepository()
    // endregion

    // region Public API

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // endregion

    private fun shouldStartSearching(query: String): Boolean {
        val stateValue = state.value
        val retVal = when (stateValue) {
            is State.FinishedWithSuccess -> {
                stateValue.query != query
            }

            is State.FinishedWithError -> {
                stateValue.query != query
            }

            is State.Idle -> {
                true
            }
            else -> false
        }

        return retVal
    }
}
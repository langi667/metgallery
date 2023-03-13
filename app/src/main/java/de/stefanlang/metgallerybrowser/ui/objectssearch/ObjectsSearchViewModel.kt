package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.repositories.ObjectsSearchRepository
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class ObjectsSearchViewModel : ViewModel() {

    // region Types

    sealed class State {

        object Idle : State()

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

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Idle)
    private val repository = ObjectsSearchRepository()

    val state = searchQuery
        .debounce(1000)
        .onEach { _isSearching.update { true } }
        .distinctUntilChanged()
        .map{ query ->
            repository.cancel()

            if (query.isEmpty()) {
                return@map State.Idle
            }

            repository.search(query)
            var retVal: State = State.Idle
            repository.result?.getOrNull()?.let { objectSearch ->
                retVal = State.FinishedWithSuccess(objectSearch)
            }

            repository.result?.exceptionOrNull()?.let {error ->
                retVal = State.FinishedWithError(error, query)
            }

            return@map retVal
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
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
package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.repositories.ObjectsSearchRepository
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.network.NetworkError
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
    private val repository = ObjectsSearchRepository()

    val state = searchQuery
        .debounce(1000)
        .distinctUntilChanged()
        .combine(repository.isSearching) { query, isSearching ->
            if (query.isEmpty()) {
                repository.cancel()
                return@combine State.Idle
            }

            // query has changed and the repository is not busy, starting to search
            if (!isSearching && repository.query != query){
                repository.search(query)
                return@combine State.Loading(query)
            }

            // current search is going on
            if (isSearching && query == repository.query) {
                return@combine State.Loading(query)
            }

            // current search is going on, but the query changed in between
            if (isSearching && query != repository.query) {
                repository.search(query)
                return@combine State.Loading(query)
            }

            // done searching, computing result
            if (!isSearching && repository.query == query && repository.result != null){
                repository.result?.getOrNull()?.let { objectSearch ->
                    return@combine State.FinishedWithSuccess(objectSearch)
                }

                repository.result?.exceptionOrNull()?.let {error ->
                    return@combine State.FinishedWithError(error, query)
                }

                return@combine State.FinishedWithError(NetworkError.InvalidState, query)
            }

            return@combine State.Idle
        }
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
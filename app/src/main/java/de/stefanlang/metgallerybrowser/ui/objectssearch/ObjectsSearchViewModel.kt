package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearch
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.ui.navigation.NavUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ObjectsSearchViewModel : ViewModel() {

    // region Types

    sealed class State {

        object Idle : State()

        class FinishedWithSuccess(val objectsSearch: METObjectsSearch) : State() {
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
    val state = _state.asStateFlow()

    private val repository = METObjectsSearchRepository()

    private val currentSearch: METObjectsSearch?
        get() {
            val stateSearchResults = state.value as? State.FinishedWithSuccess ?: return null
            val retVal = stateSearchResults.objectsSearch

            return retVal
        }

    // endregion

    // region Public API

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun startSearch() {
        if (searchQuery.value.isEmpty()) {
            _state.value = State.Idle
            return
        }

        val currentSearch = this.currentSearch

        if (currentSearch != null // query is the same and we already have results, nothing to do so far
            && currentSearch.hasResult
            && currentSearch.query == searchQuery.value
        ) {
            return
        }

        viewModelScope.launch {
            performSearch()
        }
    }

    fun onObjectIDSelected(objectID: Int, navController: NavController) {
        NavUtil.navigateToObjectDetail(navController, objectID)
    }

    // endregion

    // region Private API

    private suspend fun performSearch() {
        _isSearching.update { true }

        repository.fetch(_searchQuery.value)
        var newState: State = State.Idle

        repository.latestSearchRequest.value.search?.let { objectSearch ->
            newState = State.FinishedWithSuccess(objectSearch)
        }

        repository.latestSearchRequest.value.error?.let { error ->
            newState = State.FinishedWithError(error, searchQuery.value)
        }

        _state.value = newState
        _isSearching.update { false }
    }

    // endregion
}
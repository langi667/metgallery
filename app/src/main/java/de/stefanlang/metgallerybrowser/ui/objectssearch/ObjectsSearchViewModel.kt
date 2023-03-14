package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.ui.navigation.Navigation.navigateToObjectDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ObjectsSearchViewModel : ViewModel() {

    // region Types

    sealed class State {

        object Idle : State()

        // TODO: test
        class FinishedWithSuccess(val objectsSearch: METObjectsSearchResult) : State() {
            val hasSearchResults: Boolean
                get() {
                    val isEmpty = objectsSearch.objectIDs?.isEmpty() ?: true
                    val retVal = !isEmpty
                    return retVal
                }
        }

        class FinishedWithError(val error: Throwable) : State()
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

    // endregion

    // region Public API

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchClear() {
        _searchQuery.value = ""
        _state.update { State.Idle }
    }

    fun startSearch() {
        if (searchQuery.value.isEmpty()) {
            _state.value = State.Idle
            return
        }

        viewModelScope.launch() {
            performSearch()
        }
    }

    fun onObjectIDSelected(objectID: Int, navController: NavController) {
        navController.navigateToObjectDetail(navController, objectID)
    }

    // endregion

    // region Private API

    private suspend fun performSearch() {
        _isSearching.update { true }

        repository.fetch(_searchQuery.value)
        var newState: State = State.Idle

        repository.latest.value.result?.getOrNull()?.let { objectSearch ->
            newState = State.FinishedWithSuccess(objectSearch)
        }

        repository.latest.value.error?.let { error ->
            newState = State.FinishedWithError(error)
        }

        _state.value = newState
        _isSearching.update { false }
    }

    // endregion
}
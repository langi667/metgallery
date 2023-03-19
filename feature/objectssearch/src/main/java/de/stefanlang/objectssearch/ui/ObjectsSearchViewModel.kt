package de.stefanlang.objectssearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stefanlang.core.domain.navigation.navigateToObjectDetail
import de.stefanlang.objectssearch.model.METObjectsSearchResult
import de.stefanlang.objectssearch.repository.METObjectsSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectsSearchViewModel @Inject constructor(
    private val repository: METObjectsSearchRepository
) : ViewModel() {

    // region Types

    sealed class State {

        object Idle : State()

        class FinishedWithSuccess(val objectsSearch: METObjectsSearchResult) : State() {
            val hasSearchResults: Boolean
                get() {
                    val isEmpty = objectsSearch.objectIDs?.isEmpty() ?: true
                    val retVal = !isEmpty
                    return retVal
                }
        }

        class FinishedWithError() : State()
    }

    // endregion

    // region Properties

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Idle)
    val state = _state.asStateFlow()

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

        val search = repository.searchForObjectsWithQuery(_searchQuery.value)
        val newState: State = stateForResult(search)

        _state.value = newState
        _isSearching.update { false }
    }

    private fun stateForResult(result: Result<METObjectsSearchResult>?): State {
        val retVal = if (result != null) {
            val searchResult = result.getOrNull()
            val error = result.exceptionOrNull()

            if (searchResult != null) {
                State.FinishedWithSuccess(searchResult)
            } else if (error != null) {
                State.FinishedWithError()
            } else {
                State.Idle
            }
        } else {
            State.Idle
        }

        return retVal
    }

    // endregion
}
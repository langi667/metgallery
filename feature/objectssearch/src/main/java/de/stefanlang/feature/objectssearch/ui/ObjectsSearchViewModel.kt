package de.stefanlang.feature.objectssearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stefanlang.core.domain.navigation.navigateToObjectDetail
import de.stefanlang.feature.objectssearch.model.METObjectsSearchResult
import de.stefanlang.feature.objectssearch.repository.METObjectsSearchRepository
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

    sealed class UiState {

        object Idle : UiState()

        class FinishedWithSuccess(val objectsSearch: METObjectsSearchResult) : UiState() {
            val hasSearchResults: Boolean
                get() {
                    val isEmpty = objectsSearch.objectIDs?.isEmpty() ?: true
                    val retVal = !isEmpty
                    return retVal
                }
        }

        class FinishedWithError() : UiState()
    }

    // endregion

    // region Properties

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // endregion

    // region Public API

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchClear() {
        _searchQuery.value = ""
        _uiState.update { UiState.Idle }
    }

    fun startSearch() {
        if (searchQuery.value.isEmpty()) {
            _uiState.value = UiState.Idle
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
        val newState: UiState = stateForResult(search)

        _uiState.value = newState
        _isSearching.update { false }
    }

    private fun stateForResult(result: Result<METObjectsSearchResult>?): UiState {
        val retVal = if (result != null) {
            val searchResult = result.getOrNull()
            val error = result.exceptionOrNull()

            if (searchResult != null) {
                UiState.FinishedWithSuccess(searchResult)
            } else if (error != null) {
                UiState.FinishedWithError()
            } else {
                UiState.Idle
            }
        } else {
            UiState.Idle
        }

        return retVal
    }

    // endregion
}
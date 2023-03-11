package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import kotlinx.coroutines.flow.*

class ObjectsSearchViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _search = MutableStateFlow(ObjectsSearch())

    val search = searchText
        .debounce(1000)
        .distinctUntilChanged()
        .map { query ->
            if (query.isEmpty()) {
                return@map ObjectsSearch()
            }

            val url = METAPIURLBuilder.objectsSearchURL(query);
            val result = NetworkAPI.get(url)

            val response = result.getOrElse { error ->
                throw error
            }

            val searchResult = JSONParser.mapObjectFrom<ObjectsSearchResult>(response.data)
            val retVal = ObjectsSearch(query, searchResult)

            return@map retVal

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _search.value
        )

    fun onSearchQueryChanged(query: String) {
        _searchText.value = query
    }
    // TODO: rename to _searchQuery

}
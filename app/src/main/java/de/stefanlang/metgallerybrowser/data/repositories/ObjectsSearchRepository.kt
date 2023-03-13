package de.stefanlang.metgallerybrowser.data.repositories

import androidx.compose.runtime.MutableState
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.coroutineContext

// TODO: Singleton
class ObjectsSearchRepository {

    // region Properties

    var query: String = ""
        private set

    var result: Result<ObjectsSearch>? = null
        private set

    private var currJob: Job? = null

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // endregion

    // region Public API

    fun cancel() {
        currJob?.cancel()
        currJob = null

        query = ""
        result = null

        _isSearching.update { false }
    }
    suspend fun search(query: String) {
        this.query = query
        //_isSearching.update { true }

        val url = METAPIURLBuilder.objectsSearchURL(query);
        val result = NetworkAPI.get(url)
        val newResult = resultForResponse(result)

        this.result = newResult
       // _isSearching.update { false }
    }

    // endregion

    // region Private API

    private fun resultForResponse(responseResult: Result<NetworkResponse>): Result<ObjectsSearch> {
        val response = responseResult.getOrNull()
        val retVal = if (response != null) {
            val searchResultRaw = JSONParser.mapObjectFrom<ObjectsSearchResult>(response.data)
            val searchResult = ObjectsSearch(query, searchResultRaw)

            Result.success(searchResult)
        } else {
            responseResult.exceptionOrNull()?.let { error ->
                Result.failure(error)
            } ?: Result.failure(NetworkError.InvalidState)
        }

        return retVal
    }

    // endregion
}
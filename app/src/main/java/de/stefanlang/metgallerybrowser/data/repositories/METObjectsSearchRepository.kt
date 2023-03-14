package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.models.METObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class METObjectsSearchRepository : Repository<String, METObjectsSearchResult>() {

    // region Types

    data class Entry(
        val query: String = "",
        val result: Result<METObjectsSearch>? = null
    ) {
        val search: METObjectsSearch?
            get() {
                val retVal = result?.getOrNull()
                return retVal
            }

        val error: Throwable?
            get() {
                val retVal = result?.exceptionOrNull()
                return retVal
            }
    }

    // endregion

    // region Properties

    private var _latestSearch = MutableStateFlow(Entry())
    var latestSearchRequest = _latestSearch.asStateFlow()

    // endregion

    // region Private API

    override suspend fun performFetch(query: String) {
        val url = METAPIURLBuilder.objectsSearchURL(query);
        val result = NetworkAPI.get(url)
        val newResult = resultForResponse(query, result)

        val search = Entry(query, newResult)
        _latestSearch.value = search
    }

    private fun resultForResponse(
        query: String,
        responseResult: Result<NetworkResponse>
    ): Result<METObjectsSearch> {
        val response = responseResult.getOrNull()
        val retVal = if (response != null) {
            val searchResultRaw = mapObjectFrom<METObjectsSearchResult>(response.data)
            val searchResult = METObjectsSearch(query, searchResultRaw)

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
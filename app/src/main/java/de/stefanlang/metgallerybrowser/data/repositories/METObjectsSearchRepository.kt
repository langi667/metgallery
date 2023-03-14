package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>

class METObjectsSearchRepository : SingleEntryRepository<String, METObjectsSearchResult>() {

    // region Private API

    override suspend fun performFetch(query: String) {
        val url = METAPIURLBuilder.objectsSearchURL(query);
        val result = NetworkAPI.get(url)
        val newResult = resultForResponse(query, result)

        val search = METObjectsSearchRepositoryEntry(query, newResult)

        latest = search
    }

    private fun resultForResponse(
        query: String,
        responseResult: Result<NetworkResponse>
    ): Result<METObjectsSearchResult> {
        val response = responseResult.getOrNull()
        val retVal = if (response != null) {
            val searchResult = mapObjectFrom<METObjectsSearchResult>(response.data)
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
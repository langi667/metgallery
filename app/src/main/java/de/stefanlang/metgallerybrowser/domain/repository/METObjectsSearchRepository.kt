package de.stefanlang.metgallerybrowser.domain.repository

import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.repository.METObjectsSearchRepositoryInterface
import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.metgallerybrowser.data.repository.SingleEntryRepository
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>

class METObjectsSearchRepository : SingleEntryRepository<String, METObjectsSearchResult>(),
    METObjectsSearchRepositoryInterface {

    // region Private API

    // TODO: test case
    override suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>? {
        fetch(query)
        val retVal = entryForQuery(query)?.result

        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: String) {
        val url = METAPIURLBuilder.objectsSearchURL(query)
        val result = NetworkAPI.get(url)
        val newResult = resultForResponse(result)

        val search = METObjectsSearchRepositoryEntry(query, newResult)
        latest = search
    }

    private fun resultForResponse(
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
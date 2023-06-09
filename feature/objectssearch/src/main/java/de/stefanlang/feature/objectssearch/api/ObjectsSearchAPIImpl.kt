package de.stefanlang.feature.objectssearch.api

import de.stefanlang.core.domain.JSONParser
import de.stefanlang.core.domain.METAPIURLBuilder
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.core.network.model.NetworkResponse
import de.stefanlang.feature.objectssearch.model.METObjectsSearchResult

import javax.inject.Inject

class ObjectsSearchAPIImpl @Inject constructor(private val networkAPI: NetworkAPI) :
    ObjectsSearchAPI {
    override suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult> {
        val url = METAPIURLBuilder.objectsSearchURL(query)
        val response = networkAPI.get(url)

        val retVal = createResultForResponse(response)
        return retVal
    }

    private fun createResultForResponse(
        networkResponse: Result<NetworkResponse>,
        fallbackError: NetworkError = NetworkError.Unknown
    ): Result<METObjectsSearchResult> {
        val response = networkResponse.getOrNull()
        val error = networkResponse.exceptionOrNull()

        val retVal = if (error != null) {
            Result.failure(error)
        } else if (response != null) {
            val obj = mapObjectFrom<METObjectsSearchResult>(response.data)
            Result.success(obj)
        } else {
            Result.failure(fallbackError)
        }

        return retVal
    }

    private inline fun <reified T> mapObjectFrom(byteArray: ByteArray): T {
        return JSONParser.mapObjectFrom(byteArray)
    }
}
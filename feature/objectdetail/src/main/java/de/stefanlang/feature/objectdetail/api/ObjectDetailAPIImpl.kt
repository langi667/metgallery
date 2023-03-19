package de.stefanlang.feature.objectdetail.api

import de.stefanlang.core.domain.JSONParser
import de.stefanlang.core.domain.METAPIURLBuilder
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.core.network.model.NetworkResponse
import de.stefanlang.feature.objectdetail.model.METObject

import javax.inject.Inject

class ObjectDetailAPIImpl @Inject constructor(private val networkAPI: NetworkAPI) :
    ObjectDetailAPI {
    override suspend fun objectForID(objectID: Int): Result<METObject> {
        val url = METAPIURLBuilder.objectURL(objectID)
        val response = networkAPI.get(url)

        val retVal = createResultForResponse<METObject>(response, NetworkError.NotFound)
        return retVal
    }

    private inline fun <reified T> createResultForResponse(
        networkResponse: Result<NetworkResponse>,
        fallbackError: NetworkError = NetworkError.Unknown
    ): Result<T> {
        val response = networkResponse.getOrNull()
        val error = networkResponse.exceptionOrNull()

        val retVal = if (error != null) {
            Result.failure(error)
        } else if (response != null) {
            val obj = mapObjectFrom<T>(response.data)
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

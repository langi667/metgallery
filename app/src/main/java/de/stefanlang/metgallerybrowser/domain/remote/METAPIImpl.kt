package de.stefanlang.metgallerybrowser.domain.remote

import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError

class METAPIImpl : METAPI {
    override suspend fun objectForID(objectID: Int): Result<METObject> {
        val url = METAPIURLBuilder.objectURL(objectID)
        val result = NetworkAPI.get(url)

        val response = result.getOrNull()
        val error = result.exceptionOrNull()

        val retVal = if (error != null) {
            Result.failure(error)
        } else if (response != null) {
            val metObject = mapObjectFrom<METObject>(response.data)
            Result.success(metObject)
        } else {
            Result.failure(NetworkError.Unknown)
        }

        return retVal
    }

    override suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult> {
        // TODO: implement
        return Result.failure(NetworkError.NotFound)
    }

    override suspend fun image(url: String): Result<ImageLoadResult> {
        // TODO: implement
        return Result.failure(NetworkError.NotFound)
    }

    private inline fun <reified T> mapObjectFrom(byteArray: ByteArray): T {
        return JSONParser.mapObjectFrom(byteArray)
    }
}
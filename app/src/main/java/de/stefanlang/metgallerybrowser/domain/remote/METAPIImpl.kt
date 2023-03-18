package de.stefanlang.metgallerybrowser.domain.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse

class METAPIImpl : METAPI {
    override suspend fun objectForID(objectID: Int): Result<METObject> {
        val url = METAPIURLBuilder.objectURL(objectID)
        val response = NetworkAPI.get(url)

        val retVal = createResultForResponse<METObject>(response, NetworkError.NotFound)
        return retVal
    }

    override suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult> {
        val url = METAPIURLBuilder.objectsSearchURL(query)
        val response = NetworkAPI.get(url)

        val retVal = createResultForResponse<METObjectsSearchResult>(response)
        return retVal

    }

    override suspend fun imageForURL(url: String): Result<Bitmap> {
        val response = NetworkAPI.get(url)
        val retVal = imageForResponse(response)

        return retVal
    }

    private fun imageForResponse(networkResponse: Result<NetworkResponse>): Result<Bitmap> {
        val imageData = networkResponse.getOrNull()?.data
        val error = networkResponse.exceptionOrNull()

        val retVal = if (error != null) {
            Result.failure(error)
        } else if (imageData != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            if (bitmap == null) {
                Result.failure(NetworkError.Decode)
            } else {
                Result.success(bitmap)
            }
        } else {
            Result.failure(NetworkError.Unknown)
        }

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
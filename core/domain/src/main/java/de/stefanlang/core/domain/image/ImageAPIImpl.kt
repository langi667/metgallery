package de.stefanlang.core.domain.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.core.network.model.NetworkResponse
import javax.inject.Inject

class ImageAPIImpl @Inject constructor(val networkAPI: NetworkAPI) : ImageAPI {

    // region Public API

    override suspend fun imageForURL(url: String): Result<Bitmap> {
        val response = networkAPI.get(url)
        val retVal = imageForResponse(response)

        return retVal
    }

    // endregion

    // region Private API

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

    // endregion
}
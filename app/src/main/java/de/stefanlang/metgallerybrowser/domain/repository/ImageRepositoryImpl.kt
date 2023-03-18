package de.stefanlang.metgallerybrowser.domain.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.stefanlang.metgallerybrowser.data.repository.ImageRepository
import de.stefanlang.metgallerybrowser.data.repository.MultiEntryRepository
import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import javax.inject.Inject

typealias ImageRepositoryEntry = Repository.Entry<String, Bitmap>

class ImageRepositoryImpl @Inject constructor(maxImages: Int) :
    MultiEntryRepository<String, Bitmap>(maxImages),
    ImageRepository {

    // region Properties

    val images: List<ImageRepositoryEntry>
        get() = entries

    // endregion

    // region Public API

    override suspend fun fetchImage(url: String): Result<Bitmap>? {
        fetch(url)

        val retVal = entryForQuery(url)?.result
        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: String) {
        val result = loadImage(query)
        add(query, result)
    }

    private suspend fun loadImage(url: String): Result<Bitmap> {
        val cachedImage = cachedImageForURL(url)
        cachedImage?.let {
            return Result.success(cachedImage)
        }

        val result = NetworkAPI.get(url)
        val imageData = result.getOrNull()?.data ?: return Result.failure(
            result.exceptionOrNull() ?: NetworkError.Unknown
        )

        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        val retVal: Result<Bitmap> = if (bitmap == null) {
            Result.failure(NetworkError.Decode)
        } else {
            Result.success(bitmap)
        }
        return retVal
    }

    private fun cachedImageForURL(url: String): Bitmap? {
        val entry = entryForQuery(url)
        val retVal = entry?.result?.getOrNull()

        return retVal
    }

    // endregion
}
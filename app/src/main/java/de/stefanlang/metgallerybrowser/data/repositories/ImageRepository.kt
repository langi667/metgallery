package de.stefanlang.metgallerybrowser.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.IntRange
import androidx.compose.runtime.mutableStateListOf
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.update

typealias ImageRepositoryEntry = Repository.Entry<String, Bitmap>

class ImageRepository(@IntRange(1) val maxCachedImages: Int = 10) : Repository<String, Bitmap>() {

    // region Properties

    private val cachedImages = mutableListOf<ImageRepositoryEntry>()

    // endregion

    suspend fun fetchImage(url: String, cache: Boolean = true): Result<Bitmap> {
        val cachedImage = cachedImageForURL(url)
        cachedImage?.let {
            return Result.success(cachedImage)
        }

        val result = NetworkAPI.get(url)
        val imageData = result.getOrNull()?.data ?: return Result.failure(
            result.exceptionOrNull() ?: NetworkError.Unknown
        )
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

        if (cache) {
            cacheImage(bitmap, url)
        }

        return Result.success(bitmap)
    }

    // region Private API

    override suspend fun performFetch(url: String) {
        fetchImage(url, true)
    }

    private fun cachedImageForURL(url: String): Bitmap? {
        val retVal = cachedImages.firstOrNull { currItem ->
            currItem.query == url
        }

        return retVal?.result?.getOrNull()
    }

    private fun cacheImage(image: Bitmap, key: String) {
        reduceCacheIfNeeded()
        val item = ImageRepositoryEntry(key, Result.success(image))

        cachedImages.add(item)
    }

    private fun reduceCacheIfNeeded() {
        if (cachedImages.size < maxCachedImages) {
            return
        }

        while (cachedImages.size >= maxCachedImages) {
            cachedImages.removeAt(0)
        }
    }

    // endregion
}
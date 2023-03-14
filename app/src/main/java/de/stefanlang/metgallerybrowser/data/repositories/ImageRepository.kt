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

    private val _cachedImages = mutableStateListOf<ImageRepositoryEntry>()
    val cachedImages = _cachedImages.asFlow()

    // endregion

    // region Private API

    override suspend fun performFetch(url: String) {
        val fetchedImageResult = fetchImage(url, true)
        val entry = ImageRepositoryEntry(url, fetchedImageResult)

        _latest.update { entry }
    }

    private fun cachedImageForURL(url: String): Bitmap? {
        val retVal = _cachedImages.firstOrNull { currItem ->
            currItem.query == url
        }

        return retVal?.result?.getOrNull()
    }

    private suspend fun fetchImage(url: String, cache: Boolean = true): Result<Bitmap> {
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

    private fun cacheImage(image: Bitmap, key: String) {
        reduceCacheIfNeeded()
        val item = ImageRepositoryEntry(key, Result.success(image))

        _cachedImages.add(item)
    }

    private fun reduceCacheIfNeeded() {
        if (_cachedImages.size < maxCachedImages) {
            return
        }

        while (_cachedImages.size >= maxCachedImages) {
            _cachedImages.removeAt(0)
        }
    }

    // endregion
}
package de.stefanlang.metgallerybrowser.data.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.IntRange
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError


typealias ImageRepositoryEntry = Repository.Entry<String, Bitmap>

// TODO: multi Value Repository

class ImageRepository(@IntRange(1) val maxCachedImages: Int = 10) : Repository<String, Bitmap>() {

    // region Properties

    val images: List<ImageRepositoryEntry>
        get() = entries

    private val entries = mutableListOf<ImageRepositoryEntry>()

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
        val retVal: Result<Bitmap> = if (bitmap == null) {
            Result.failure(NetworkError.Decode)
        }
        else {
            Result.success(bitmap)
        }

        if (cache) {
            add(url, retVal)
        }

        return retVal
    }

    // region Private API

    override suspend fun performFetch(query: String) {
        fetchImage(query, true)
    }

    private fun cachedImageForURL(url: String): Bitmap? {
        val retVal = entries.firstOrNull { currItem ->
            currItem.query == url
        }

        return retVal?.result?.getOrNull()
    }

    private fun add(entry: ImageRepositoryEntry) {
        reduceCacheIfNeeded()
        entries.add(entry)
    }
    private fun add(query: String, result: Result<Bitmap>) {
        add(ImageRepositoryEntry(query, result))
    }

    private fun reduceCacheIfNeeded() {
        if (entries.size < maxCachedImages) {
            return
        }

        while (entries.size >= maxCachedImages) {
            entries.removeAt(0)
        }
    }

    // endregion
}
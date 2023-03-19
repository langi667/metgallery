package de.stefanlang.metgallerybrowser.repository.image

import android.graphics.Bitmap
import de.stefanlang.metgallerybrowser.api.METAPI
import de.stefanlang.repository.MultiEntryRepository
import de.stefanlang.repository.Repository
import javax.inject.Inject

typealias ImageRepositoryEntry = Repository.Entry<String, Bitmap>

class ImageRepositoryImpl @Inject constructor(maxImages: Int, val api: METAPI) :
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

    override suspend fun performFetch(query: String): ImageRepositoryEntry {
        val result = loadImage(query)
        val retVal = ImageRepositoryEntry(query, result)

        return retVal
    }

    private suspend fun loadImage(url: String): Result<Bitmap> {
        val cachedImage = cachedImageForURL(url)
        cachedImage?.let {
            return Result.success(cachedImage)
        }

        val retVal = api.imageForURL(url)
        return retVal
    }

    private fun cachedImageForURL(url: String): Bitmap? {
        val entry = entryForQuery(url)
        val retVal = entry?.result?.getOrNull()

        return retVal
    }

    // endregion
}
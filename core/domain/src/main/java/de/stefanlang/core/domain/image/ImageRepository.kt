package de.stefanlang.core.domain.image

import android.graphics.Bitmap

interface ImageRepository {
    suspend fun fetchImage(url: String): Result<Bitmap>?
}
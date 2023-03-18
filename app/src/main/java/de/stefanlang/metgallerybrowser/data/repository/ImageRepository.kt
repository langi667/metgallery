package de.stefanlang.metgallerybrowser.data.repository

import android.graphics.Bitmap

interface ImageRepository {
    suspend fun fetchImage(url: String): Result<Bitmap>?
}
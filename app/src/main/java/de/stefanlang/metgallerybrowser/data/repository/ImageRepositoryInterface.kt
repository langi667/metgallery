package de.stefanlang.metgallerybrowser.data.repository

import android.graphics.Bitmap

interface ImageRepositoryInterface {
    suspend fun fetchImage(url: String): Result<Bitmap>?
}
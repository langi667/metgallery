package de.stefanlang.core.api

import android.graphics.Bitmap

interface ImageAPI {

    suspend fun imageForURL(url: String): Result<Bitmap>
}
package de.stefanlang.core.domain.image

import android.graphics.Bitmap

interface ImageAPI {

    suspend fun imageForURL(url: String): Result<Bitmap>
}
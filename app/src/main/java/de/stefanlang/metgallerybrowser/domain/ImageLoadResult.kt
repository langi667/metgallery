package de.stefanlang.metgallerybrowser.domain

import android.graphics.Bitmap

sealed class ImageLoadResult(val url: String) {
    class Success(url: String, val image: Bitmap): ImageLoadResult(url)
    class Failure(url: String, val error: Throwable): ImageLoadResult(url)
}
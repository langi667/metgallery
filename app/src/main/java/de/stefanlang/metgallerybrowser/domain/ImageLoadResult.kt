package de.stefanlang.metgallerybrowser.domain

import android.graphics.Bitmap

sealed class ImageLoadResult(val url: String) {
    class Success(url: String, val image: Bitmap) : ImageLoadResult(url)

    // commended out code is left purposely, in case error is needed
    class Failure(url: String/*, val error: Throwable*/) : ImageLoadResult(url)
}
package de.stefanlang.metgallerybrowser.domain

import android.graphics.Bitmap

sealed class ImageLoadResult(val url: String) {
    class Success(url: String, val image: Bitmap) : ImageLoadResult(url) {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (javaClass != other?.javaClass) {
                return false
            }

            if (!super.equals(other)) {
                return false
            }

            other as Success

            if (image != other.image) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + image.hashCode()
            return result
        }
    }

    // commended out code is left purposely, in case error is needed
    class Failure(url: String/*, val error: Throwable*/) : ImageLoadResult(url) {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            if (javaClass != other?.javaClass) {
                return false
            }

            if (!super.equals(other)) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return url.hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as ImageLoadResult

        if (url != other.url) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}

fun List<ImageLoadResult>.imageLoadResultForImage(image: Bitmap): ImageLoadResult.Success? {
    val retVal = this.firstOrNull { currLoadResult ->
        if (currLoadResult is ImageLoadResult.Success) {
            currLoadResult.image == image
        } else {
            false
        }
    } as? ImageLoadResult.Success

    return retVal
}

fun List<ImageLoadResult>.indexOfResultForImage(image: Bitmap): Int {
    val imageLoadResult = imageLoadResultForImage(image) ?: return -1
    val retVal = indexOf(imageLoadResult)

    return retVal
}

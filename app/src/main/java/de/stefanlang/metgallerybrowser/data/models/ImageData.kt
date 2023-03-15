package de.stefanlang.metgallerybrowser.data.models

data class ImageData(
    val imageURL: String,
    val isPrimary: Boolean,
    val smallImageURL: String? = null
) {
    // TODO: test case
    fun containsURL(url: String?): Boolean {
        val retVal = if (url == null) {
            false
        } else {
            imageURL == url || smallImageURL == url
        }

        return retVal
    }
}


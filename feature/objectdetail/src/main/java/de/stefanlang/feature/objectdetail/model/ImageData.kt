package de.stefanlang.feature.objectdetail.model

data class ImageData(
    val imageURL: String,
    val isPrimary: Boolean,
    val smallImageURL: String? = null
) {
    fun containsURL(url: String?): Boolean {
        val retVal = if (url == null) {
            false
        } else {
            imageURL == url || smallImageURL == url
        }

        return retVal
    }
}


package de.stefanlang.metgallerybrowser.data.models

data class ImageData(
    val imageURL: String,
    val isPrimary: Boolean,
    val smallImageURL: String? = null
)
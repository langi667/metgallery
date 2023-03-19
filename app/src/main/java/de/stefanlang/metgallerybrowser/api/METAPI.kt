package de.stefanlang.metgallerybrowser.api

import android.graphics.Bitmap

interface METAPI {

    suspend fun imageForURL(url: String): Result<Bitmap>
}
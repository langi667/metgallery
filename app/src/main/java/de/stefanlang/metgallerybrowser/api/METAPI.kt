package de.stefanlang.metgallerybrowser.api

import android.graphics.Bitmap
import de.stefanlang.metgallerybrowser.models.METObject
import de.stefanlang.metgallerybrowser.models.METObjectsSearchResult

interface METAPI {
    suspend fun objectForID(objectID: Int): Result<METObject>
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
    suspend fun imageForURL(url: String): Result<Bitmap>
}
package de.stefanlang.metgallerybrowser.data.remote

import android.graphics.Bitmap
import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult

interface METAPI {
    suspend fun objectForID(objectID: Int): Result<METObject>
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
    suspend fun imageForURL(url: String): Result<Bitmap>
}
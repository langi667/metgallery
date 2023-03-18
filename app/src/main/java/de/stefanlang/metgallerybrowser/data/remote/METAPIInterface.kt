package de.stefanlang.metgallerybrowser.data.remote

import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult

interface METAPIInterface {
    suspend fun objectForID(objectID: Int): Result<METObject>
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
    suspend fun image(url: String): Result<ImageLoadResult>
}
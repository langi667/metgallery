package de.stefanlang.metgallerybrowser.search.api

import de.stefanlang.metgallerybrowser.search.model.METObjectsSearchResult

interface ObjectsSearchAPI {
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
}
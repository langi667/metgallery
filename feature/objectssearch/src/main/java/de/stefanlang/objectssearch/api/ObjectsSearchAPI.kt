package de.stefanlang.objectssearch.api

import de.stefanlang.objectssearch.model.METObjectsSearchResult

interface ObjectsSearchAPI {
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
}
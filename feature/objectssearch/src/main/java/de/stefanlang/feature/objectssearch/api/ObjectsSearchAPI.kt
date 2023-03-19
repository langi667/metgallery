package de.stefanlang.feature.objectssearch.api

import de.stefanlang.feature.objectssearch.model.METObjectsSearchResult

interface ObjectsSearchAPI {
    suspend fun objectIDsForSearchQuery(query: String): Result<METObjectsSearchResult>
}
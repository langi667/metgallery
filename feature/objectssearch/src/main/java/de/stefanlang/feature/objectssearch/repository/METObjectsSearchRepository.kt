package de.stefanlang.feature.objectssearch.repository

import de.stefanlang.feature.objectssearch.model.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
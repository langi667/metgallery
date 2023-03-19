package de.stefanlang.objectssearch.repository

import de.stefanlang.objectssearch.model.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
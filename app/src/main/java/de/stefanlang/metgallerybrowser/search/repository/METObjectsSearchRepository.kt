package de.stefanlang.metgallerybrowser.search.repository

import de.stefanlang.metgallerybrowser.search.model.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
package de.stefanlang.metgallerybrowser.repository.metobject

import de.stefanlang.metgallerybrowser.models.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
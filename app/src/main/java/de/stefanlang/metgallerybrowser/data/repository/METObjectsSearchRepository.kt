package de.stefanlang.metgallerybrowser.data.repository

import de.stefanlang.metgallerybrowser.domain.models.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
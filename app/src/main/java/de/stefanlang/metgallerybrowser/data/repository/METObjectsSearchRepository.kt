package de.stefanlang.metgallerybrowser.data.repository

import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult

interface METObjectsSearchRepository {
    suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>?
}
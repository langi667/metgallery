package de.stefanlang.objectssearch.repository

import de.stefanlang.objectssearch.api.ObjectsSearchAPI
import de.stefanlang.objectssearch.model.METObjectsSearchResult
import de.stefanlang.repository.Repository
import de.stefanlang.repository.SingleEntryRepository
import javax.inject.Inject

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>

class METObjectsSearchRepositoryImpl @Inject constructor(val api: ObjectsSearchAPI) :
    SingleEntryRepository<String, METObjectsSearchResult>(),
    METObjectsSearchRepository {

    // region Private API

    override suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>? {
        val retVal = fetch(query).result
        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: String): METObjectsSearchRepositoryEntry {
        val newResult = api.objectIDsForSearchQuery(query)
        val retVal =
            de.stefanlang.objectssearch.repository.METObjectsSearchRepositoryEntry(query, newResult)

        return retVal
    }

    // endregion
}
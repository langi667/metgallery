package de.stefanlang.metgallerybrowser.repository.metobject

import de.stefanlang.metgallerybrowser.api.METAPI
import de.stefanlang.metgallerybrowser.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.repository.Repository
import de.stefanlang.metgallerybrowser.repository.SingleEntryRepository
import javax.inject.Inject

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>

class METObjectsSearchRepositoryImpl @Inject constructor(val api: METAPI) :
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
        val retVal = METObjectsSearchRepositoryEntry(query, newResult)

        return retVal
    }

    // endregion
}
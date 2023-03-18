package de.stefanlang.metgallerybrowser.domain.repository

import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.repository.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.metgallerybrowser.data.repository.SingleEntryRepository
import de.stefanlang.metgallerybrowser.domain.models.METObjectsSearchResult
import javax.inject.Inject

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>

class METObjectsSearchRepositoryImpl @Inject constructor(val api: METAPI) :
    SingleEntryRepository<String, METObjectsSearchResult>(),
    METObjectsSearchRepository {

    // region Private API

    // TODO: test case
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
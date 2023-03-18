package de.stefanlang.metgallerybrowser.domain.repository

import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.repository.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.metgallerybrowser.data.repository.SingleEntryRepository
import javax.inject.Inject

typealias METObjectsSearchRepositoryEntry = Repository.Entry<String, METObjectsSearchResult>


class METObjectsSearchRepositoryImpl @Inject constructor(val api: METAPI) :
    SingleEntryRepository<String, METObjectsSearchResult>(),
    METObjectsSearchRepository {

    // region Private API

    // TODO: test case
    override suspend fun searchForObjectsWithQuery(query: String): Result<METObjectsSearchResult>? {
        fetch(query)
        val retVal = entryForQuery(query)?.result

        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: String) {
        val newResult = api.objectIDsForSearchQuery(query)

        val search = METObjectsSearchRepositoryEntry(query, newResult)
        latest = search
    }

    // endregion
}
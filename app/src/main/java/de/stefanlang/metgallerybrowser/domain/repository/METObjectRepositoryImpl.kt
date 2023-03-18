package de.stefanlang.metgallerybrowser.domain.repository

import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.repository.METObjectRepository
import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.metgallerybrowser.data.repository.SingleEntryRepository
import de.stefanlang.network.NetworkError
import javax.inject.Inject

typealias METObjectsRepositoryEntry = Repository.Entry<Int, METObject>

class METObjectRepositoryImpl @Inject constructor(val api: METAPI) :
    SingleEntryRepository<Int, METObject>(),
    METObjectRepository {

    // region Public API

    // TODO: test case
    override suspend fun fetchObjectForResult(id: Int): Result<METObject>? {
        fetch(id)

        val retVal = entryForQuery(id)?.result
        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: Int) {
        val result = api.objectForID(query)

        val entry = entryForResponse(query, result)
        latest = entry
    }

    private fun entryForResponse(
        objectID: Int,
        networkResult: Result<METObject>
    ): METObjectsRepositoryEntry {
        val retVal: METObjectsRepositoryEntry
        val metObject = networkResult.getOrNull()
        val error = networkResult.exceptionOrNull()

        retVal = if (metObject != null && metObject.isValid) {
            Entry(objectID, Result.success(metObject))
        } else if (error != null) {
            Entry(objectID, Result.failure(error))
        } else {
            Entry(objectID, Result.failure(NetworkError.NotFound))
        }

        return retVal
    }

    // endregion
}
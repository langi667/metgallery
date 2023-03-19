package de.stefanlang.feature.objectdetail.repository

import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.feature.objectdetail.api.ObjectDetailAPI
import de.stefanlang.feature.objectdetail.model.METObject

import de.stefanlang.repository.Repository
import de.stefanlang.repository.SingleEntryRepository
import javax.inject.Inject

typealias METObjectsRepositoryEntry = Repository.Entry<Int, METObject>

class METObjectRepositoryImpl @Inject constructor(val api: ObjectDetailAPI) :
    SingleEntryRepository<Int, METObject>(),
    METObjectRepository {

    // region Public API

    override suspend fun fetchObjectForResult(id: Int): Result<METObject>? {
        val retVal = fetch(id).result
        return retVal
    }

    // endregion

    // region Private API

    override suspend fun performFetch(query: Int): METObjectsRepositoryEntry {
        val result = api.objectForID(query)
        val retVal = entryForResponse(query, result)

        return retVal
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
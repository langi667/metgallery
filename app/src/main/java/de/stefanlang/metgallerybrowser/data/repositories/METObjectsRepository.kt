package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse

typealias METObjectsRepositoryEntry = Repository.Entry<Int, METObject>

class METObjectsRepository : SingleEntryRepository<Int, METObject>() {

    // region Private API

    override suspend fun performFetch(objectID: Int) {
        val url = METAPIURLBuilder.objectURL(objectID);
        val result = NetworkAPI.get(url)

        val entry = entryForResponse(objectID, result)
        latest = entry
    }

    private fun entryForResponse(
        objectID: Int,
        networkResult: Result<NetworkResponse>
    ): METObjectsRepositoryEntry {
        val retVal: METObjectsRepositoryEntry
        val response = networkResult.getOrNull();

        retVal = if (response != null) {
            val metObject = mapObjectFrom<METObject>(response.data)
            Entry(objectID, Result.success(metObject))
        } else {
            val error = networkResult.exceptionOrNull() ?: NetworkError.InvalidState
            Entry(objectID, Result.failure(error))
        }

        return retVal
    }

    // endregion
}
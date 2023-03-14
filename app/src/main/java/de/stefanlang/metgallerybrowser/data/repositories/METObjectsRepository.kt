package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.models.METObject
import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI
import de.stefanlang.network.NetworkError
import de.stefanlang.network.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class METObjectsRepository : Repository<Int, METObject>() {

    // region Types

    data class Entry(
        val query: Int = 0, // TODO: invalid id
        val result: Result<METObject>? = null
    ) {
        val isValid: Boolean
            get() {
                val retVal = query != 0
                return retVal
            }

        val completed: Boolean
            get() {
                val retVal = result != null
                return retVal
            }

        val isSuccess: Boolean
            get() {
                val retVal = result?.isSuccess ?: false
                return retVal
            }

        val hasError: Boolean
            get() {
                val retVal = result?.isFailure ?: false
                return retVal
            }

        val error: Throwable?
            get() {
                val retVal = result?.exceptionOrNull()
                return retVal
            }
    }

    // endregion

    // region Properties

    private var _latest = MutableStateFlow(Entry())
    var latest = _latest.asStateFlow()

    // endregion

    // region Private API

    override suspend fun performFetch(objectID: Int) {
        val url = METAPIURLBuilder.objectURL(objectID);
        val result = NetworkAPI.get(url)

        val entry = entryForResponse(objectID, result)
        _latest.value = entry
    }

    private fun entryForResponse(objectID: Int, networkResult: Result<NetworkResponse>): Entry {
        val retVal: Entry
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
package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

abstract class Repository<QUERY, RESULT> {

    // region Types

    // TODO: tests
    data class Entry<Q, R>(
        val query: Q? = null,
        val result: Result<R>? = null
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

        val resultValue: R?
            get() {
                val retVal = result?.getOrNull()
                return retVal
            }
    }

    // endregion

    suspend fun fetch(query: QUERY) = withContext(Dispatchers.IO) {
        performFetch(query)
    }

    protected abstract suspend fun performFetch(query: QUERY)

    protected inline fun <reified RESULT> mapObjectFrom(byteArray: ByteArray): RESULT {
        return JSONParser.mapper.readValue(byteArray, RESULT::class.java)
    }
}

// TODO: move to separate file

abstract class SingleEntryRepository<QUERY, RESULT>: Repository<QUERY, RESULT>() {

    // region Properties
    var latest = Entry<QUERY, RESULT>()
        protected set
    // endregion
}
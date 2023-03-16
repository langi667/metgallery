package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Repository<QUERY, RESULT> {

    // region Types

    data class Entry<Q, R>(
        val query: Q? = null,
        val result: Result<R>? = null
    ) {
        val isSuccess: Boolean
            get() {
                val retVal = result?.isSuccess ?: false
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
        return JSONParser.mapObjectFrom(byteArray)
    }
}

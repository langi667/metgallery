package de.stefanlang.metgallerybrowser.repository

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

    abstract fun entryForQuery(query: QUERY): Entry<QUERY, RESULT>?
    protected abstract fun storeEntry(entry: Entry<QUERY, RESULT>)

    suspend fun fetch(query: QUERY): Entry<QUERY, RESULT> {
        val retVal = withContext(Dispatchers.IO) {
            val entry = performFetch(query)
            storeEntry(entry)

            return@withContext entry
        }

        return retVal
    }

    protected abstract suspend fun performFetch(query: QUERY): Entry<QUERY, RESULT>

    protected fun matchesQuery(entry: Entry<QUERY, RESULT>, query: QUERY): Boolean {
        val retVal = entry.query == query
        return retVal
    }
}

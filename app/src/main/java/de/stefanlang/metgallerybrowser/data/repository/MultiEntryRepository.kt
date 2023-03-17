package de.stefanlang.metgallerybrowser.data.repository

import androidx.annotation.IntRange

// TODO: Consider either 0 or null as no limits
abstract class MultiEntryRepository<QUERY, RESULT>(@IntRange(1) val maxEntries: Int = 15) :
    Repository<QUERY, RESULT>() {

    // region Properties

    protected val entries = mutableListOf<Entry<QUERY, RESULT>>()

    // endregion

    // region Public API

    override fun entryForQuery(query: QUERY): Entry<QUERY, RESULT>? {
        val retVal = entries.firstOrNull { currEntry ->
            currEntry.query == query
        }

        return retVal
    }

    // endregion

    // region Private API
    protected fun add(query: QUERY, result: Result<RESULT>) {
        add(Entry(query, result))
    }

    private fun add(entry: Entry<QUERY, RESULT>) {
        if (entries.contains(entry)) { // already added
            return
        }

        reduceIfNeeded()
        entries.add(entry)
    }

    private fun reduceIfNeeded() {
        if (entries.size < maxEntries) {
            return
        }

        while (entries.size >= maxEntries) {
            entries.removeAt(0)
        }
    }

    // endregion
}
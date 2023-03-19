package de.stefanlang.repository

abstract class SingleEntryRepository<QUERY, RESULT> : Repository<QUERY, RESULT>() {

    // region Properties

    var latest = Entry<QUERY, RESULT>()
        protected set

    // endregion

    // region Private API

    override fun storeEntry(entry: Entry<QUERY, RESULT>) {
        latest = entry
    }

    final override fun entryForQuery(query: QUERY): Entry<QUERY, RESULT>? {
        val retVal = if (matchesQuery(latest, query)) {
            latest
        } else {
            null
        }
        return retVal
    }

    // endregion
}
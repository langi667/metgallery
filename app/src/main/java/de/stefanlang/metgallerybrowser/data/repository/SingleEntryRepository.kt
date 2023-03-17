package de.stefanlang.metgallerybrowser.data.repository

abstract class SingleEntryRepository<QUERY, RESULT> : Repository<QUERY, RESULT>() {

    // region Properties

    var latest = Entry<QUERY, RESULT>()
        protected set

    // endregion

    final override fun entryForQuery(query: QUERY): Entry<QUERY, RESULT>? {
        val retVal = if (matchesQuery(latest, query)) {
            latest
        } else {
            null
        }
        return retVal
    }
}
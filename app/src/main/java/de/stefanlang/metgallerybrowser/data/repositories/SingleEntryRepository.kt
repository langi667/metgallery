package de.stefanlang.metgallerybrowser.data.repositories

abstract class SingleEntryRepository<QUERY, RESULT> : Repository<QUERY, RESULT>() {

    // region Properties

    var latest = Entry<QUERY, RESULT>()
        protected set

    // endregion
}
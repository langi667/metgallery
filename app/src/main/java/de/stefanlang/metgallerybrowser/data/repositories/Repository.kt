package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Repository<QUERY, RESULT> {
    suspend fun fetch(query: QUERY) {
        withContext(Dispatchers.IO) {
            performFetch(query)
        }
    }

    protected abstract suspend fun performFetch(query: QUERY)

    protected inline fun <reified RESULT> mapObjectFrom(byteArray: ByteArray): RESULT {
        return JSONParser.mapper.readValue(byteArray, RESULT::class.java)
    }
}
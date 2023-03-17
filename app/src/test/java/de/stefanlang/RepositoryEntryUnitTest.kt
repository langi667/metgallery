package de.stefanlang

import de.stefanlang.metgallerybrowser.data.repository.Repository
import de.stefanlang.network.NetworkError
import org.junit.Assert.*
import org.junit.Test

typealias TestRepoEntry = Repository.Entry<Int, String>

class RepositoryEntryUnitTest {
    val successEntry = TestRepoEntry(1, Result.success("success"))
    val errorEntry = TestRepoEntry(1, Result.failure(NetworkError.InvalidState))

    @Test
    fun test_isSuccess() {
        var entry = TestRepoEntry()
        assertFalse(entry.isSuccess)

        entry = successEntry
        assertTrue(entry.isSuccess)

        entry = errorEntry
        assertFalse(entry.isSuccess)
    }

    @Test
    fun test_error() {
        var entry = errorEntry
        assertNotNull(entry.error)

        entry = successEntry
        assertNull(entry.error)
    }

    @Test
    fun test_resultValue() {
        var entry = errorEntry
        assertNull(entry.resultValue)

        entry = successEntry
        assertNotNull(entry.resultValue)
        assertNull(entry.error)
    }
}
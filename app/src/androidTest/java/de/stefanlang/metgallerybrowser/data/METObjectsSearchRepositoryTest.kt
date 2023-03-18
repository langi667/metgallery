package de.stefanlang.metgallerybrowser.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.metgallerybrowser.NetworkInstrumentedTest
import de.stefanlang.metgallerybrowser.domain.repository.METObjectsSearchRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class METObjectsSearchRepositoryTest : NetworkInstrumentedTest() {
    @Test
    fun testSearchEmpty() {
        val repo = METObjectsSearchRepositoryImpl()
        assertNull(repo.latest.query)
        assertNull(repo.latest.result)
    }

    @Test
    fun testSearchSuccess() {
        val repo = METObjectsSearchRepositoryImpl()

        runBlocking {
            val search = "sunflower"
            repo.fetch(search)

            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, search)

            assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
        }
    }

    @Test
    fun testSearchNoResults() {
        val repo = METObjectsSearchRepositoryImpl()

        runBlocking {
            val query = "343hdfberj"
            repo.fetch(query)

            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, query)

            assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total == 0)
        }
    }

    @Test
    fun testSearchSuccessSpecialChars() {
        val repo = METObjectsSearchRepositoryImpl()

        runBlocking {
            val query = "Karl H. Müller"
            repo.fetch(query)

            val searchResult = repo.latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
        }
    }
}
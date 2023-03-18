package de.stefanlang.metgallerybrowser.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.metgallerybrowser.HiltInstrumentedTest
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.domain.repository.METObjectsSearchRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class METObjectsSearchRepositoryTest : HiltInstrumentedTest() {
    @Inject
    lateinit var api: METAPI
    private val failQuery = "343hdfberj"
    private val successQuery = "sunflower"

    @Test
    fun testSearchForObjectsWithQuerySuccess() {
        val repo = createRepo()

        runBlocking {
            val search = successQuery
            val result = repo.searchForObjectsWithQuery(search)
            assertNotNull(result)

            assertTrue(result?.isSuccess ?: false)
            val searchResult = result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
            assertEquals(result, repo.latest.result)
        }
    }

    @Test
    fun testSearchForObjectsWithQueryFailure() {
        val repo = createRepo()

        runBlocking {
            val search = failQuery
            val result = repo.searchForObjectsWithQuery(search)
            assertNotNull(result)

            assertTrue(result?.isSuccess ?: false)
            val searchResult = result?.getOrNull()
            assertEquals(searchResult?.total, 0)
        }
    }

    @Test
    fun testSearchEmpty() {
        val repo = createRepo()
        assertNull(repo.latest.query)
        assertNull(repo.latest.result)
    }

    @Test
    fun testSearchSuccess() {
        val repo = createRepo()

        runBlocking {
            val search = successQuery
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
        val repo = createRepo()

        runBlocking {
            val query = failQuery
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
        val repo = createRepo()

        runBlocking {
            val query = "Karl H. Müller"
            repo.fetch(query)

            val searchResult = repo.latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
        }
    }

    private fun createRepo(): METObjectsSearchRepositoryImpl {
        val retVal = METObjectsSearchRepositoryImpl(api)
        return retVal
    }
}
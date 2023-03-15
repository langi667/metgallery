package de.stefanlang.metgallerybrowser

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class METObjectsSearchRepositoryInstrumentedTest: NetworkInstrumentedTest() {
    @Test
    fun testSearchEmpty(){
        val repo =  METObjectsSearchRepository()
        assertNull(repo.latest.query)
        assertNull(repo.latest.result)
    }
    @Test
    fun testSearchSuccess(){
        val repo = METObjectsSearchRepository()

        runBlocking {
            val url = METAPIURLBuilder.objectsSearchURL("sunflower")
            assertTrue(url.isNotBlank())

            repo.fetch(url)
            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, url)

            assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
        }
    }

    @Test
    fun testSearchNoResults(){
        val repo = METObjectsSearchRepository()

        runBlocking {
            val url = METAPIURLBuilder.objectsSearchURL("343hdfberj")
            assertTrue(url.isNotBlank())

            repo.fetch(url)
            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, url)

            assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total == 0)
        }
    }

    @Test
    fun testSearchSuccessSpecialChars(){
        val repo = METObjectsSearchRepository()

        runBlocking {
            val url = METAPIURLBuilder.objectsSearchURL("Karl H. Müller")
            assertTrue(url.isNotBlank())

            repo.fetch(url)
            val searchResult = repo.latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertTrue(searchResult!!.total > 0)
            assertEquals(searchResult.objectIDs!!.size, searchResult.total)
        }
    }
}
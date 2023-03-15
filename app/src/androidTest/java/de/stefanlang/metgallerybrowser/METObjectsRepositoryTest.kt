package de.stefanlang.metgallerybrowser

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.metgallerybrowser.data.repositories.METObjectsRepository
import de.stefanlang.metgallerybrowser.domain.Defines
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class METObjectsRepositoryTest : NetworkInstrumentedTest() {

    @Test
    fun testEmpty() {
        val repo = METObjectsRepository()
        Assert.assertNull(repo.latest.query)
        Assert.assertNull(repo.latest.result)
    }

    @Test
    fun testFetchSuccess() {
        val repo = METObjectsRepository()

        runBlocking {
            val objectID = 253343
            repo.fetch(objectID)

            val latest = repo.latest
            Assert.assertNotNull(latest.query)
            Assert.assertEquals(latest.query, objectID)

            Assert.assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            Assert.assertNotNull(searchResult)

            Assert.assertEquals(searchResult!!.objectID, objectID)
        }
    }

    @Test
    fun testFetchNoResult() {
        val repo = METObjectsRepository()

        runBlocking {
            val objectID = Defines.InvalidID
            repo.fetch(objectID)

            val latest = repo.latest
            Assert.assertNotNull(latest.query)
            Assert.assertEquals(latest.query, objectID)

            Assert.assertTrue(latest.result?.isFailure ?: false)
            val searchResult = latest.result?.getOrNull()
            Assert.assertNull(searchResult)

            val error = latest.result?.exceptionOrNull()
            Assert.assertNotNull(error)
        }
    }
}
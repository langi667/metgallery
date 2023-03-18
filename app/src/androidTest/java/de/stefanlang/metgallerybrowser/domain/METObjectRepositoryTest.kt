package de.stefanlang.metgallerybrowser.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.metgallerybrowser.HiltInstrumentedTest
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.domain.repository.METObjectRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class METObjectRepositoryTest : HiltInstrumentedTest() {

    @Inject
    lateinit var api: METAPI
    private val existingObjectID = 253343

    @Test
    fun testFetchObjectForResultSuccess() {
        val repo = createRepo()
        runBlocking {
            val result = repo.fetchObjectForResult(existingObjectID)
            assertNotNull(result)
            result!!

            assertTrue(result.isSuccess)

            val metObject = result.getOrNull()
            assertNotNull(metObject)
            metObject!!

            assertTrue(metObject.title == "Chalcedony scaraboid")
            assertEquals(metObject, repo.latest.result?.getOrNull())
        }
    }

    fun testFetchObjectForResultFailure() {
        val repo = createRepo()
        runBlocking {
            val result = repo.fetchObjectForResult(Defines.InvalidID)
            assertNotNull(result)
            result!!

            assertFalse(result.isSuccess)
            assertNull(result.getOrNull())
        }
    }

    @Test
    fun testEmpty() {
        val repo = createRepo()
        assertNull(repo.latest.query)
        assertNull(repo.latest.result)
    }

    @Test
    fun testFetchSuccess() {
        val repo = createRepo()
        runBlocking {
            repo.fetch(existingObjectID)

            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, existingObjectID)

            assertTrue(latest.result?.isSuccess ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNotNull(searchResult)

            assertEquals(searchResult!!.objectID, existingObjectID)
        }
    }

    @Test
    fun testFetchNoResult() {
        val repo = createRepo()
        runBlocking {
            val objectID = Defines.InvalidID
            repo.fetch(objectID)

            val latest = repo.latest
            assertNotNull(latest.query)
            assertEquals(latest.query, objectID)

            assertTrue(latest.result?.isFailure ?: false)
            val searchResult = latest.result?.getOrNull()
            assertNull(searchResult)

            val error = latest.result?.exceptionOrNull()
            assertNotNull(error)
        }
    }

    private fun createRepo(): METObjectRepositoryImpl {
        val retVal = METObjectRepositoryImpl(api)
        return retVal
    }
}
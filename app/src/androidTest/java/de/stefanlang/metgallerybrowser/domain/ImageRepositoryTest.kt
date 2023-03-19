package de.stefanlang.metgallerybrowser.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.metgallerybrowser.HiltInstrumentedTest
import de.stefanlang.metgallerybrowser.repository.image.ImageRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ImageRepositoryTest : HiltInstrumentedTest() {

    @Inject
    lateinit var api: de.stefanlang.core.api.ImageAPI

    private lateinit var repo: ImageRepositoryImpl

    @Test
    fun testFetchImageSuccess() {
        runBlocking {
            val result =
                repo.fetchImage("https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg")
            assertNotNull(result)
            assertTrue(result!!.isSuccess)
            assertNotNull(result.getOrNull())
        }
    }

    @Test
    fun testFetchImageFailInvalidURL() {
        runBlocking {
            val result = repo.fetchImage("https://no/address")
            assertNotNull(result)
            assertTrue(result!!.isFailure)
            assertNotNull(result.exceptionOrNull())
        }
    }

    @Test
    fun testFetchImageFailNoImage() {
        runBlocking {
            val result = repo.fetchImage("https://google.com")
            assertNotNull(result)

            assertTrue(result!!.isFailure)
            assertNotNull(result.exceptionOrNull())
        }
    }

    @Test
    fun testFetchImageCache() {
        val images = listOf(
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337524.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337525.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337526.jpg",
        )
        val repo = ImageRepositoryImpl(images.size - 1, api)

        runBlocking {
            var result = repo.fetchImage(images.first())
            assertTrue(result!!.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(repo.images.size, 1)

            result = repo.fetchImage(images[1])
            assertTrue(result!!.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(repo.images.size, 2)

            result = repo.fetchImage(images.first()) // this is already cached
            assertTrue(result!!.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(repo.images.size, 2)

            images.forEach { currURL ->
                val imageFetchResult = repo.fetchImage(currURL)
                assertTrue(imageFetchResult!!.isSuccess)
                assertNotNull(imageFetchResult.getOrNull())
            }

            assertTrue(repo.images.size < images.size)

            images.forEachIndexed { index, url ->
                val cachedImage = repo.images.find { currEntry ->
                    currEntry.query == url
                }

                if (index == 0) { // cache removes the first one, so this is supposed to be dropped
                    assertNull(cachedImage)
                } else {
                    assertNotNull(cachedImage)
                }
            }
        }
    }

    @Test
    fun testFetchImageCacheWithFailures() {
        val existing = "https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg"
        val nonExisting = "https://images.metmuseum.org/CRDImages/gr/web-large/blablabla"
        val invalid = "https://google.com"

        val repo = ImageRepositoryImpl(3, api)

        runBlocking {
            var result = repo.fetchImage(existing)

            assertTrue(result!!.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals(repo.images.size, 1)

            result = repo.fetchImage(nonExisting)

            assertTrue(result!!.isFailure)
            assertNull(result.getOrNull())
            assertEquals(repo.images.size, 2)

            result = repo.fetchImage(invalid)

            assertTrue(result!!.isFailure)
            assertNull(result.getOrNull())
            assertEquals(repo.images.size, 3)
        }
    }

    @Test
    fun testEntries() {
        val url = "https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg"
        val urlSecond = "https://images.metmuseum.org/CRDImages/gr/web-large/DP337524.jpg"

        val repo = ImageRepositoryImpl(1, api)
        assertNull(repo.entryForQuery(query = url))

        runBlocking {
            repo.fetch(url)
            assertNotNull(repo.entryForQuery(query = url))

            repo.fetch(urlSecond)
            assertNotNull(repo.entryForQuery(query = urlSecond))

            // should be removed due to max cache size = 1
            assertNull(repo.entryForQuery(query = url))
        }
    }

    override fun handleInjectionsDone() {
        repo = ImageRepositoryImpl(15, api)
    }
}
package de.stefanlang.metgallerybrowser

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageRepositoryInstrumentedTest: NetworkInstrumentedTest() {

    @Test
    fun testFetchImageSuccess() {
        val repo = ImageRepository()
        runBlocking {
            val result = repo.fetchImage("https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg")
            Assert.assertTrue(result.isSuccess)
            Assert.assertNotNull(result.getOrNull())
        }
    }

    @Test
    fun testFetchImageFailInvalidURL() {
        val repo = ImageRepository()
        runBlocking {
            val result = repo.fetchImage("https://no/address")
            Assert.assertTrue(result.isFailure)
            Assert.assertNotNull(result.exceptionOrNull())
        }
    }

    @Test
    fun testFetchImageFailNoImage() {
        val repo = ImageRepository()

        runBlocking {
            val result = repo.fetchImage("https://google.com")
            Assert.assertTrue(result.isFailure)
            Assert.assertNotNull(result.exceptionOrNull())
        }
    }

    @Test
    fun testFetchImageCache() {
        val images = listOf("https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337524.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337525.jpg",
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP337526.jpg",
        )

        val repo = ImageRepository(images.size - 1)

        runBlocking {
            var result = repo.fetchImage(images.first(), true)
            Assert.assertTrue(result.isSuccess)
            Assert.assertNotNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 1)

            result = repo.fetchImage(images[1], true)
            Assert.assertTrue(result.isSuccess)
            Assert.assertNotNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 2)

            result = repo.fetchImage(images.first(), true) // this is already cached
            Assert.assertTrue(result.isSuccess)
            Assert.assertNotNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 2)

            images.forEach { currURL ->
                val result = repo.fetchImage(currURL)
                Assert.assertTrue(result.isSuccess)
                Assert.assertNotNull(result.getOrNull())
            }

            Assert.assertTrue (repo.images.size < images.size )

            images.forEachIndexed { index, url ->
                val cachedImage = repo.images.find { currEntry ->
                    currEntry.query == url
                }

                if (index == 0) { // cache removes the first one, so this is supposed to be dropped
                    Assert.assertNull(cachedImage)
                }
                else {
                    Assert.assertNotNull(cachedImage)
                }
            }
        }
    }

    @Test
    fun testFetchImageCacheWithFailures() {
        val existing = "https://images.metmuseum.org/CRDImages/gr/web-large/DP337517.jpg"
        val nonExisting = "https://images.metmuseum.org/CRDImages/gr/web-large/blablabla"
        val invalid = "https://google.com"

        val repo = ImageRepository(3)

        runBlocking {
            var result = repo.fetchImage(existing, true)

            Assert.assertTrue(result.isSuccess)
            Assert.assertNotNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 1)

            result = repo.fetchImage(nonExisting, true)

            Assert.assertTrue(result.isFailure)
            Assert.assertNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 2)

            result = repo.fetchImage(invalid, true)

            Assert.assertTrue(result.isFailure)
            Assert.assertNull(result.getOrNull())
            Assert.assertEquals (repo.images.size, 3)
        }

    }
}
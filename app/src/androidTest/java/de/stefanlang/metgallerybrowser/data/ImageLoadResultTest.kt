package de.stefanlang.metgallerybrowser.data

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.core.network.model.NetworkError
import de.stefanlang.feature.objectdetail.model.imageLoadResultForImage
import de.stefanlang.feature.objectdetail.model.indexOfResultForImage
import de.stefanlang.metgallerybrowser.NetworkInstrumentedTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageLoadResultTest : NetworkInstrumentedTest() {
    private val testImage: Bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ALPHA_8)
    private val testImage2: Bitmap = Bitmap.createBitmap(20, 40, Bitmap.Config.ALPHA_8)

    @Test
    fun testImageLoadResultForImage() {
        val list = mutableListOf<de.stefanlang.feature.objectdetail.model.ImageLoadResult>()
        assertTrue(list.imageLoadResultForImage(testImage) == null)

        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Failure(
                "",
                NetworkError.InvalidState
            )
        )
        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Success(
                "http.//www.url-one.com",
                testImage2
            )
        )
        assertTrue(list.imageLoadResultForImage(testImage) == null)

        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Success(
                "http.//www.url-two.com",
                testImage
            )
        )
        assertTrue(list.imageLoadResultForImage(testImage) != null)
    }

    @Test
    fun testIndexOfResultForImage() {
        val list = mutableListOf<de.stefanlang.feature.objectdetail.model.ImageLoadResult>()
        assertTrue(list.indexOfResultForImage(testImage) == -1)

        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Failure(
                "",
                NetworkError.InvalidState
            )
        )
        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Success(
                "http.//www.url-one.com",
                testImage2
            )
        )
        assertTrue(list.indexOfResultForImage(testImage) == -1)

        list.add(
            de.stefanlang.feature.objectdetail.model.ImageLoadResult.Success(
                "http.//www.url-two.com",
                testImage
            )
        )
        assertTrue(list.indexOfResultForImage(testImage) == 2)
    }

}
package de.stefanlang

import de.stefanlang.metgallerybrowser.data.models.ImageData
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ImageDataUnitTest {
    @Test
    fun test_containsURL() {
        val testURL1 = "http://d.android.com/tools/testing"
        val testURL2 = "http://e.android.com/tools/testing"
        val testURL3 = "http://f.android.com/tools/testing"

        val dataAll = ImageData(testURL1, true, testURL2)

        assertTrue(dataAll.containsURL(testURL1))
        assertTrue(dataAll.containsURL(testURL2))
        assertFalse(dataAll.containsURL(testURL3))

        val dataPrimaryOnly = ImageData(testURL1, true, null)
        assertTrue(dataPrimaryOnly.containsURL(testURL1))
        assertFalse(dataPrimaryOnly.containsURL(testURL2))

        val dataSecondaryOnly = ImageData("", true, testURL2)
        assertFalse(dataSecondaryOnly.containsURL(testURL1))
        assertTrue(dataSecondaryOnly.containsURL(testURL2))
    }
}
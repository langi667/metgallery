package de.stefanlang

import de.stefanlang.metgallerybrowser.Defines
import de.stefanlang.metgallerybrowser.models.METObject
import org.junit.Assert.*
import org.junit.Test

class METObjectUnitTest {

    @Test
    fun test_isValid() {
        val objInvalid1 = METObject()
        assertFalse(objInvalid1.isValid)

        val objInvalid2 = METObject()
        objInvalid2.objectID = Defines.InvalidID

        assertFalse(objInvalid2.isValid)

        val objInvalid3 = METObject()
        objInvalid3.objectID = 12345

        assertTrue(objInvalid3.isValid)
    }

    @Test
    fun test_imageDate() {
        val testPrimaryURL = "http://d.android.com/tools/testing"
        val testSecondaryOneURL = "http://e.android.com/tools/testing"

        val testSecondaryTwoURL = "http://f.android.com/tools/testing"
        val testNotAddedURL = "http://g.android.com/tools/testing"

        val testObject = METObject()

        testObject.primaryImage = testPrimaryURL
        testObject.additionalImages = listOf(testSecondaryOneURL, testSecondaryTwoURL)

        val imgDataForPrimary = testObject.imageData.firstOrNull {
            it.containsURL(testPrimaryURL)
        }
        assertNotNull(imgDataForPrimary)

        val imgDataForSecOne = testObject.imageData.firstOrNull {
            it.containsURL(testSecondaryOneURL)
        }
        assertNotNull(imgDataForSecOne)

        val imgDataForSecTwo = testObject.imageData.firstOrNull {
            it.containsURL(testSecondaryTwoURL)
        }
        assertNotNull(imgDataForSecTwo)

        val noData = testObject.imageData.firstOrNull {
            it.containsURL(testNotAddedURL)
        }
        assertNull(noData)
    }
}
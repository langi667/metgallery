package de.stefanlang

import de.stefanlang.core.domain.Defines
import org.junit.Assert.*
import org.junit.Test

class METObjectUnitTest {

    @Test
    fun test_isValid() {
        val objInvalid1 = de.stefanlang.feature.objectdetail.model.METObject()
        assertFalse(objInvalid1.isValid)

        val objInvalid2 = de.stefanlang.feature.objectdetail.model.METObject()
        objInvalid2.objectID = Defines.InvalidID

        assertFalse(objInvalid2.isValid)

        val objInvalid3 = de.stefanlang.feature.objectdetail.model.METObject()
        objInvalid3.objectID = 12345

        assertTrue(objInvalid3.isValid)
    }

    @Test
    fun test_imageDate() {
        val testPrimaryURL = "http://d.android.com/tools/testing"
        val testSecondaryOneURL = "http://e.android.com/tools/testing"

        val testSecondaryTwoURL = "http://f.android.com/tools/testing"
        val testNotAddedURL = "http://g.android.com/tools/testing"

        val testObject = de.stefanlang.feature.objectdetail.model.METObject()

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
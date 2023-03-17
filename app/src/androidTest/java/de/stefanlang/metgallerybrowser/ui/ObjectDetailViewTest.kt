package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import de.stefanlang.metgallerybrowser.*
import de.stefanlang.metgallerybrowser.ui.common.Tags
import de.stefanlang.metgallerybrowser.ui.objectdetail.ObjectDetailView
import org.junit.Rule
import org.junit.Test

class ObjectDetailViewTest : NetworkInstrumentedTest() {

    private val objectID: Int = 253343


    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testLoadingState() {
        setupRule()
        rule.onNodeWithText(getString(R.string.loading_state_hint)).assertExists()
        rule.onNodeWithTag(Tags.PROGRESSBAR.name).assertExists()
    }

    @Test
    fun testNotFound() {
        setupRule(-1)
        rule.waitUntilFoundWithText(Timeout.LONG, getString(R.string.no_results_state_hint))
    }

    @Test
    fun testFound() {
        setupRule()

        rule.waitUntilFoundWithText(Timeout.LONG, "Chalcedony scaraboid")
        rule.waitUntilFoundWithText(Timeout.LONG, "Greek and Roman Art")
        rule.waitAndFindAtLeastWithTag(Timeout.LONG, Tags.GALLERY_IMAGE_PREVIEW.name, 3)
    }

    @Test
    fun testGallery() {
        setupRule()
        val contentDescription = "https://images.metmuseum.org/CRDImages/gr/web-large/DP328403.jpg"

        rule.waitUntilFoundWithContentDescription(Timeout.VERY_LONG, contentDescription)
        rule.onNodeWithContentDescription(contentDescription).performClick().assertExists()
        rule.waitUntilFoundWithTag(Timeout.MEDIUM, Tags.GALLERY_SELECTED_IMAGE.name)

        rule.onNodeWithTag(Tags.GALLERY_SELECTED_IMAGE.name).performClick()
        rule.waitUntilNotFoundWithTag(Timeout.LONG, Tags.GALLERY_SELECTED_IMAGE.name)

        rule.onNodeWithContentDescription(contentDescription).performClick().assertExists()
        rule.waitUntilFoundWithTag(Timeout.MEDIUM, Tags.GALLERY_SELECTED_IMAGE.name)

        rule.onNodeWithTag(Tags.BACK_BUTTON.name).performClick()
        rule.waitUntilNotFoundWithTag(Timeout.LONG, Tags.GALLERY_SELECTED_IMAGE.name)
    }

    private fun setupRule(objectID: Int = this.objectID) {
        rule.setContent {
            ObjectDetailView(
                navController = rememberNavController(),
                objectID = objectID
            )
        }
    }
}
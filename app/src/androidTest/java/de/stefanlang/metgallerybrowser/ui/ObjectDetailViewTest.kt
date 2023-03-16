package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import de.stefanlang.metgallerybrowser.NetworkInstrumentedTest
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.ui.objectdetail.ObjectDetailView
import de.stefanlang.metgallerybrowser.ui.objectssearch.Tags
import de.stefanlang.metgallerybrowser.waitUntilFoundWithText
import org.junit.Rule
import org.junit.Test

class ObjectDetailViewTest : NetworkInstrumentedTest() {

    val objectID: Int = 253343

    @get:Rule
    val rule = createComposeRule()

    fun setupRule(objectID: Int = this.objectID) {
        rule.setContent {
            ObjectDetailView(
                navController = rememberNavController(),
                objectID = objectID
            )
        }
    }

    @Test
    fun testLoadingState() {
        setupRule()
        rule.onNodeWithText(getString(R.string.loading_state_hint)).assertExists()
        rule.onNodeWithTag(Tags.PROGRESSBAR.name).assertExists()
    }

    @Test
    fun testNotFound() {
        setupRule(-1)
        rule.waitUntilFoundWithText(5000, getString(R.string.no_results_state_hint))
    }
}
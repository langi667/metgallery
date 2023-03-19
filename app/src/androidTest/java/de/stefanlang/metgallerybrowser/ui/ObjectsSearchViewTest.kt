package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.feature.objectssearch.ui.Tags
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.waitUntilFoundWithTag
import de.stefanlang.metgallerybrowser.waitUntilFoundWithText
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ObjectsSearchViewTest : MainActivityTest() {

    @Test
    fun testIdleState() {
        rule.onNodeWithText(getString(R.string.idle_state_hint)).assertExists()
    }

    @Test
    fun testIsSearching() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("a")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntilFoundWithTag(Timeout.LONG, Tags.SEARCH_FIELD.name)
    }

    @Test
    fun testNoSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("jjrerjkejdncdskerwkrmf")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntilFoundWithText(
            Timeout.LONG,
            getString(de.stefanlang.core.api.R.string.no_results_state_hint)
        )
    }

    @Test
    fun testSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("Müller")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()
        rule.waitUntilFoundWithTag(Timeout.LONG, Tags.SEARCH_RESULTS_LIST.name)

        val isNotEmpty = rule.onAllNodesWithTag(Tags.SEARCH_RESULT_ENTRY.name)
            .fetchSemanticsNodes()
            .isNotEmpty()

        assertTrue(isNotEmpty)
    }
}

package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.*
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.ui.objectssearch.Tags
import de.stefanlang.metgallerybrowser.waitUntilFoundWithTag
import de.stefanlang.metgallerybrowser.waitUntilFoundWithText
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectsSearchViewTest : MainActivityTest() {

    @Test
    fun testIdleState() {
        rule.onNodeWithText(getString(R.string.idle_state_hint)).assertExists()
    }

    @Test
    fun testIsSearching() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("a")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntilFoundWithTag(5000, Tags.SEARCH_FIELD.name)

    }

    @Test
    fun testNoSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("jjrerjkejdncdskerwkrmf")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntilFoundWithText(5000, getString(R.string.no_results_state_hint))

    }

    @Test
    fun testSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("Müller")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()
        rule.waitUntilFoundWithTag(5000, Tags.SEARCH_RESULTS_LIST.name)

        val isNotEmpty = rule.onAllNodesWithTag(Tags.SEARCH_RESULT_ENTRY.name)
            .fetchSemanticsNodes()
            .isNotEmpty()

        assertTrue(isNotEmpty)
    }
}

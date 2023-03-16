package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.*
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.ui.objectssearch.Tags
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectsSearchScreenTest : MainActivityTest() {

    @Test
    fun testIdleState() {
        rule.onNodeWithText(getString(R.string.idle_state_hint)).assertExists()
    }

    @Test
    fun testIsSearching() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("a")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntil(5000) {
            rule
                .onAllNodesWithTag(Tags.SEARCH_FIELD.name)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testNoSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("jjrerjkejdncdskerwkrmf")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntil(5000) {
            rule
                .onAllNodesWithText(getString(R.string.no_results_state_hint))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testSearchResult() {
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performTextInput("Müller")
        rule.onNodeWithTag(Tags.SEARCH_FIELD.name).performImeAction()

        rule.waitUntil(5000) {
            rule
                .onAllNodesWithTag(Tags.SEARCH_RESULTS_LIST.name)
                .fetchSemanticsNodes().isNotEmpty()
        }

        val isNotEmpty = rule.onAllNodesWithTag(Tags.SEARCH_RESULT_ENTRY.name)
            .fetchSemanticsNodes()
            .isNotEmpty()

        assertTrue(isNotEmpty)
    }
}
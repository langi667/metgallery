package de.stefanlang.metgallerybrowser

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText


fun ComposeContentTestRule.waitUntilFoundWithTag(timeout: Long = 1000, tag: String) {
    this.waitUntil(timeout) {
        onAllNodesWithTag(tag)
            .fetchSemanticsNodes().isNotEmpty()
    }
}

fun ComposeContentTestRule.waitUntilFoundWithText(timeout: Long = 1000, text: String) {
    this.waitUntil(timeout) {
        onAllNodesWithText(text)
            .fetchSemanticsNodes().isNotEmpty()
    }
}
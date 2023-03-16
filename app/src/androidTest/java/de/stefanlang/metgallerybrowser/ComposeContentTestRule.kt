package de.stefanlang.metgallerybrowser

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText


fun ComposeContentTestRule.waitUntilFoundWithTag(timeout: Long = 1000, tag: String) {
    waitAndFindAtLeastWithTag(timeout, tag)
}

fun ComposeContentTestRule.waitAndFindAtLeastWithTag(
    timeout: Long = 1000,
    tag: String,
    atLeast: Int = 1
) {
    this.waitUntil(timeout) {
        val nodes = onAllNodesWithTag(tag)
            .fetchSemanticsNodes()

        val retVal = nodes.isNotEmpty() && nodes.size <= atLeast
        retVal
    }
}

fun ComposeContentTestRule.waitAndFindAtLeastWithText(
    timeout: Long = 1000,
    text: String,
    atLeast: Int = 1
) {
    this.waitUntil(timeout) {
        val nodes = onAllNodesWithText(text)
            .fetchSemanticsNodes()

        val retVal = nodes.isNotEmpty() && nodes.size <= atLeast
        retVal
    }
}

fun ComposeContentTestRule.waitUntilFoundWithText(timeout: Long = 1000, text: String) {
    waitAndFindAtLeastWithText(timeout, text)
}

fun ComposeContentTestRule.waitAndFindAtLeastWithContentDescription(
    timeout: Long = 1000,
    contentDescription: String,
    atLeast: Int = 1
) {
    this.waitUntil(timeout) {
        val nodes = onAllNodesWithContentDescription(contentDescription)
            .fetchSemanticsNodes()

        val retVal = nodes.isNotEmpty() && nodes.size <= atLeast
        retVal
    }
}

fun ComposeContentTestRule.waitUntilFoundWithContentDescription(
    timeout: Long = 1000,
    contentDescription: String
) {
    waitAndFindAtLeastWithContentDescription(timeout, contentDescription)
}

fun ComposeContentTestRule.waitUntilNotFoundWithTag(timeout: Long = 1000, tag: String) {
    this.waitUntil(timeout) {
        val nodes = onAllNodesWithTag(tag)
            .fetchSemanticsNodes()

        val retVal = nodes.isEmpty()
        retVal
    }
}
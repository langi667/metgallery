package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.stefanlang.metgallerybrowser.HiltInstrumentedTest
import de.stefanlang.metgallerybrowser.MainActivity
import org.junit.Rule

/**
 * Base class for all tests related to the MainActivity
 */
open class MainActivityTest : HiltInstrumentedTest() {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()


}
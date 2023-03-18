package de.stefanlang.metgallerybrowser

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

open class HiltInstrumentedTest : NetworkInstrumentedTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
        handleInjectionsDone()
    }


    /**
     * Override to spawn objects that may depend on injected properties.
     * Those are injected at this time
     */
    protected open fun handleInjectionsDone() {
    }
}
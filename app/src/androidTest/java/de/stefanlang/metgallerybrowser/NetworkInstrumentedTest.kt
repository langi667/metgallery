package de.stefanlang.metgallerybrowser

import androidx.test.platform.app.InstrumentationRegistry
import de.stefanlang.network.NetworkAPI
import org.junit.Assert

open class NetworkInstrumentedTest {
    init {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertNotNull(appContext)
        NetworkAPI.setup(appContext)
    }
}

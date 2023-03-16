package de.stefanlang.metgallerybrowser

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import de.stefanlang.network.NetworkAPI
import org.junit.Assert

open class NetworkInstrumentedTest {
    val appContext: Context

    init {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertNotNull(appContext)
        NetworkAPI.setup(appContext)
    }

    fun getString(@StringRes id: Int): String {
        val retVal = appContext.resources.getString(id)
        return retVal
    }
}

package de.stefanlang.metgallerybrowser

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert

open class NetworkInstrumentedTest {

    object Timeout {
        const val MEDIUM = 2000L
        const val LONG = 5000L
        const val VERY_LONG = 10000L
    }

    protected val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    init {
        Assert.assertNotNull(appContext)
    }

    fun getString(@StringRes id: Int): String {
        val retVal = appContext.resources.getString(id)
        return retVal
    }

    fun getString(@StringRes id: Int, varargs: Any): String {
        val retVal = appContext.resources.getString(id, varargs)
        return retVal
    }
}

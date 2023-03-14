package de.stefanlang.metgallerybrowser

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.stefanlang.network.NetworkAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NetworkInstrumentedTest {
    @Test
    fun networkAPI() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertNotNull(appContext)
        NetworkAPI.setup(appContext)

        runBlocking {
            var response = NetworkAPI.get("https://www.google.com")
            assertTrue(response.isSuccess)

            response = NetworkAPI.get("https://doesnotexists")
            assertTrue(response.isFailure)
        }
    }
}
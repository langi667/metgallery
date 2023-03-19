package de.stefanlang.metgallerybrowser.modules.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.metgallerybrowser.HiltInstrumentedTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NetworkAPITest : HiltInstrumentedTest() {

    @Inject
    lateinit var networkAPI: NetworkAPI

    @Test
    fun testNetworkAPISuccess() {
        runBlocking {
            val response = networkAPI.get("https://www.google.com")
            assertTrue(response.isSuccess)
        }
    }

    @Test
    fun testNetworkAPIFail() {
        runBlocking {
            val response = networkAPI.get("https://doesnotexists")
            assertTrue(response.isFailure)
        }
    }
}
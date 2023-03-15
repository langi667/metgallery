package de.stefanlang.metgallerybrowser

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.stefanlang.network.NetworkAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NetworkAPITest : NetworkInstrumentedTest() {
    @Test
    fun testNetworkAPISuccess() {
        runBlocking {
            val response = NetworkAPI.get("https://www.google.com")
            assertTrue(response.isSuccess)
        }
    }

    @Test
    fun testNetworkAPIFail() {
        runBlocking {
            val response = NetworkAPI.get("https://doesnotexists")
            assertTrue(response.isFailure)
        }
    }
}
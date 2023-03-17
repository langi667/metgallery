package de.stefanlang.network

import android.content.Context
import de.stefanlang.network.NetworkRequestHeader
import de.stefanlang.network.NetworkResponse
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Provides basic network communications, such as GET, POST, ...
 */
object NetworkAPI {

    // region Properties

    private val executor: Executor = Executors.newWorkStealingPool()
    private val idProvider = AtomicInteger(0);
    private val pendingRequests = mutableListOf<NetworkRequest>()

    private lateinit var cronetEngine: CronetEngine

    // endregion

    // region Public API

    fun setup(context: Context) {
        if (this::cronetEngine.isInitialized) {
            return
        }

        val builder = CronetEngine.Builder(context)
        cronetEngine = builder.build()
    }

    suspend fun get(url: String, headers: NetworkRequestHeader? = null): Result<NetworkResponse> {
        val retVal = request(url = url, method = HTTPMethod.GET, headers = headers)
        return retVal
    }

    suspend fun request(
        url: String,
        method: HTTPMethod,
        headers: NetworkRequestHeader?
    ): Result<NetworkResponse> {
        val requestId = nextId();
        assert(requestId > 0)

        val request = NetworkRequest.Builder(url, requestId, cronetEngine, executor)
            .setMethod(method)
            .addHeaders(headers)
            .build()

        return startRequest(request)
    }

    // endregion

    // region Private API

    private suspend fun startRequest(request: NetworkRequest): Result<NetworkResponse> {
        return suspendCoroutine {
            addRequest(request);

            request.start { _ ->
                if (request.isFinished) {
                    removeRequest(request)

                    val result = if (request.isSuccess) {
                        Result.success(request.response)
                    } else {
                        Result.failure(request.error ?: NetworkError.InvalidState);
                    }

                    it.resume(result);
                }
            }
        }
    }

    private fun addRequest(request: NetworkRequest): Boolean {
        if (isPendingRequest(request)) {
            return false
        }

        pendingRequests.add(request);
        return true
    }

    private fun removeRequest(request: NetworkRequest) {
        pendingRequests.remove(request);
    }

    private fun isPendingRequest(request: NetworkRequest): Boolean {
        val retVal = pendingRequests.contains(request)
        return retVal;
    }

    private fun nextId(): Int {
        val retVal = idProvider.incrementAndGet()
        return retVal
    }

    // endregion
}
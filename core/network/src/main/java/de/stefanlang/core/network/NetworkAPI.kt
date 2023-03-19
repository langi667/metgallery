package de.stefanlang.core.network

import de.stefanlang.core.network.model.*
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Provides basic network communications, such as GET, POST, ...
 */
class NetworkAPI @Inject constructor(
    private val executor: Executor,
    private val idProvider: AtomicInteger,
    private val cronetEngine: CronetEngine
) {

    // region Properties

    private val pendingRequests = mutableListOf<NetworkRequest>()

    // endregion

    // region Public API

    suspend fun get(url: String, headers: NetworkRequestHeader? = null): Result<NetworkResponse> {
        val retVal = request(url = url, method = HTTPMethod.GET, headers = headers)
        return retVal
    }

    suspend fun request(
        url: String,
        method: HTTPMethod,
        headers: NetworkRequestHeader?
    ): Result<NetworkResponse> {
        val requestId = nextId()
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
            addRequest(request)

            request.start { _ ->
                if (request.isFinished) {
                    removeRequest(request)

                    val result = if (request.isSuccess) {
                        Result.success(request.response)
                    } else {
                        Result.failure(request.error ?: NetworkError.InvalidState)
                    }

                    it.resume(result)
                }
            }
        }
    }

    private fun addRequest(request: NetworkRequest): Boolean {
        if (isPendingRequest(request)) {
            return false
        }

        pendingRequests.add(request)
        return true
    }

    private fun removeRequest(request: NetworkRequest) {
        pendingRequests.remove(request)
    }

    private fun isPendingRequest(request: NetworkRequest): Boolean {
        val retVal = pendingRequests.contains(request)
        return retVal
    }

    private fun nextId(): Int {
        val retVal = idProvider.incrementAndGet()
        return retVal
    }

    // endregion
}
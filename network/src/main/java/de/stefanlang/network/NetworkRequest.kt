package de.stefanlang.network

import de.stefanlang.network.NetworkRequestHeader
import de.stefanlang.network.NetworkResponse
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.Executor

typealias NetworkRequestProgressUpdate = (NetworkRequest.State) -> Unit;

class NetworkRequest(val id: Int) : UrlRequest.Callback() {

    // region Types

    class Builder(
        private val url: String,
        private val id: Int,
        private val cronetEngine: CronetEngine,
        private val executor: Executor
    ) {
        private var method: HTTPMethod = HTTPMethod.GET;
        private val headers: NetworkRequestHeader by lazy {
            NetworkRequestHeader()
        }

        fun build(): NetworkRequest {
            val retVal = NetworkRequest(id)
            val requestBuilder = cronetEngine.newUrlRequestBuilder(
                url,
                retVal,
                executor
            )

            requestBuilder.setHttpMethod(method.name)

            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            val request: UrlRequest = requestBuilder.build()
            retVal.request = request;

            return retVal
        }

        fun setMethod(method: HTTPMethod): Builder {
            this.method = method
            return this
        }

        fun addHeader(key: String, value: String): Builder {
            headers[key] = value
            return this
        }

        fun addHeaders(headers: NetworkRequestHeader?): Builder {
            headers?.let { headersToAdd ->
                this.headers.putAll(headersToAdd)
            }

            return this
        }
    }

    sealed class State {
        object Ready : State()
        object Started : State()
        object ReceivingResponse : State()
        object Finished : State()
    }

    // endregion

    // region Properties

    val isFinished: Boolean
        get() {
            return state == State.Finished
        }

    val isSuccess: Boolean
        get() {
            val retVal = error == null
            return retVal
        }

    val response: NetworkResponse
        get() {
            val retVal = createResponse()
            return retVal
        }

    var responseData: ByteArray = ByteArray(0)
        private set

    var error: NetworkError? = null
        private set

    var state: State = State.Ready
        private set(value) {
            if (field == value) {
                return
            }

            field = value;
            handleStateChanged();
        }

    private val bytesReceived = ByteArrayOutputStream()
    private val receiveChannel = Channels.newChannel(bytesReceived)

    private var progressUpdate: NetworkRequestProgressUpdate? = null
    private var request: UrlRequest? = null;

    // endregion

    // region Public API

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) {
            return true;
        }

        val otherAsNetworkRequest = other as? NetworkRequest ?: return false
        val retVal = id == otherAsNetworkRequest.id

        return retVal
    }

    fun start(progressUpdate: NetworkRequestProgressUpdate): Boolean {
        if (state != State.Ready) {
            return false
        }

        this.progressUpdate = progressUpdate
        request?.start()

        state = State.Started;
        return true
    }

    // endregion

    // region Private API

    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        state = State.ReceivingResponse;

        val size = (1024 * 1024)

        val buffer: ByteBuffer = ByteBuffer.allocateDirect(size)
        request?.read(buffer)
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        byteBuffer?.flip()
        receiveChannel.write(byteBuffer)

        byteBuffer?.clear()
        request?.read(byteBuffer)
    }

    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
        responseData = bytesReceived.toByteArray()
        state = State.Finished
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        this.error = NetworkError(info?.httpStatusCode ?: NetworkError.InvalidStateError)
        state = State.Finished
    }

    private fun handleStateChanged() {
        progressUpdate?.invoke(this.state)
    }

    private fun createResponse(): NetworkResponse {
        val retVal = NetworkResponse(id, responseData)
        return retVal
    }

    // endregion
}
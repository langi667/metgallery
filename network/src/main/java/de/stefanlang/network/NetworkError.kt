package de.stefanlang.network

data class NetworkError(val code: Int, val text: String = "NetworkError") : Throwable(text) {

    // region Companion

    companion object {
        const val InvalidStateError = -1
        const val UnknownNetworkError = -2

        val InvalidState =
            NetworkError(InvalidStateError, "Invalid request state ${InvalidStateError}")

        val Unknown =
            NetworkError(UnknownNetworkError, "Unknown error")
    }

    // endregion
}
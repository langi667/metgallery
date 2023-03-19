package de.stefanlang.core.network.model

data class NetworkResponse(val id: Int, val data: ByteArray) {

    // region Public API

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (javaClass != other?.javaClass) {
            return false
        }

        other as NetworkResponse

        if (id != other.id) {
            return false
        }

        if (!data.contentEquals(other.data)) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.contentHashCode()
        return result
    }

    // endregion
}

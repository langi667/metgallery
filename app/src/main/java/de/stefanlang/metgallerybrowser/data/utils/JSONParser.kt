package de.stefanlang.metgallerybrowser.data.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JSONParser {

    // region Properties

    val mapper: ObjectMapper by lazy {
        ObjectMapper()
    }

    // endregion

    // region Public API

    inline fun <reified T> mapObjectFrom(byteArray: ByteArray): T {
        return mapper.readValue(byteArray, T::class.java)
    }

    // endregion
}
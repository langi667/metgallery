package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class METObjectsSearch(
    var query: String = "",
    var result: METObjectsSearchResult? = null
) {
    // region Properties

    val hasResult: Boolean
        get() {
            val retVal = result != null
            return retVal
        }

    // endregion

}

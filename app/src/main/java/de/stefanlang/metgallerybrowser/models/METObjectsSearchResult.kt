package de.stefanlang.metgallerybrowser.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class METObjectsSearchResult(
    var total: Int = 0,
    var objectIDs: List<Int>? = null
)
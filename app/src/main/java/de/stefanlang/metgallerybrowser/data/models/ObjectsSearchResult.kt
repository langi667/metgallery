package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ObjectsSearchResult(
    var total: Int = 0,
    var objectIDs: List<Int>? = null
) {}

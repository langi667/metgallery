package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ObjectsSearch(
    var query: String = "",
    var result: ObjectsSearchResult? = null
)

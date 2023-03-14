package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class METObjectTag {
    var term: String? = null

    @JsonProperty("AAT_URL")
    var aatURL: String? = null

    @JsonProperty("Wikidata_URL")
    var wikidataURL: String? = null
}
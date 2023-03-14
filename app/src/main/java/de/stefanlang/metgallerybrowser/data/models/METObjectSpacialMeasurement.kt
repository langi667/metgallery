package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class METObjectSpacialMeasurement {

    @JsonProperty("Height")
    var height: Float? = null

    @JsonProperty("Length")
    var length: Float? = null

    @JsonProperty("Width")
    var width: Float? = null
}
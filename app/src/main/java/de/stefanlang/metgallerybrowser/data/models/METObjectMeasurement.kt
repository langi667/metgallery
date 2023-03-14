package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class METObjectMeasurement {
    var elementName: String? = null
    var elementDescription: String? = null
    var elementMeasurements: METObjectSpacialMeasurement? = null
}
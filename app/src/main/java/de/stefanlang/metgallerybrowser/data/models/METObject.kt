package de.stefanlang.metgallerybrowser.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import de.stefanlang.metgallerybrowser.domain.Defines
import de.stefanlang.metgallerybrowser.domain.METAPIURLBuilder
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)

// Dear clone/ copy constructor enjoy this one ;)
class METObject {

    // region Properties

    var objectID: Int? = null
    var accessionNumber: String? = null
    var accessionYear: String? = null

    var isPublicDomain: Boolean? = null
    var primaryImage: String? = null
    var primaryImageSmall: String? = null

    var additionalImages: List<String>? = null
    var department: String? = null
    var objectName: String? = null

    var title: String? = null
    var culture: String? = null
    var period: String? = null

    var dynasty: String? = null
    var reign: String? = null
    var portfolio: String? = null

    var artistRole: String? = null
    var artistDisplayName: String? = null
    var artistDisplayBio: String? = null

    var artistNationality: String? = null
    var artistBeginDate: String? = null
    var artistEndDate: String? = null

    var artistGender: String? = null
    @JsonProperty("artistWikidata_URL")
    var artistWikidataURL: String? = null
    @JsonProperty("artistULAN_URL")
    var artistULANURL: String? = null

    var objectDate: String? = null
    var objectBeginDate: Int? = null
    var objectEndDate: Int? = null

    var medium: String? = null
    var dimensions: String? = null
    var creditLine: String? = null

    var geographyType: String? = null
    var city: String? = null
    var state: String? = null

    var county: String? = null
    var region: String? = null
    var subregion: String? = null

    var locale: String? = null
    var locus: String? = null
    var excavation: String? = null

    var river: String? = null
    var classification: String? = null
    var rightsAndReproduction: String? = null

    var linkResource: String? = null
    var objectURL: String? = null
    @JsonProperty("objectWikidata_URL")
    var objectWikidataURL: String? = null

    var isTimelineWork: Boolean? = null

    @JsonProperty("GalleryNumber")
    var galleryNumber: String? = null

    val imageData: List<METObjectImageData>
        get() {
            return createImageDataList()
        }

    val isValid: Boolean
        get() {
            val retVal = objectID != null && objectID != Defines.InvalidID
            return retVal
        }

    // endregion

    // region Private API

    private fun createImageDataList(): List<METObjectImageData> {
        val retVal = mutableListOf<METObjectImageData>()

        imageDataFromURLs(primaryImage, true, primaryImageSmall)?.let { imageData ->
            retVal.add(imageData)
        }

        additionalImages?.forEach { currImageURL ->
            imageDataFromURLs(
                currImageURL,
                false,
                METAPIURLBuilder.smallImageURLForImageURL(currImageURL)
            )?.let { currImageData ->
                retVal.add(currImageData)
            }
        }

        return retVal
    }

    private fun imageDataFromURLs(
        imageURL: String?,
        isPrimary: Boolean,
        smallImageURL: String?
    ): METObjectImageData? {
        val retVal: METObjectImageData? = if (imageURL == null || imageURL.isBlank()) {
            null
        } else {
            METObjectImageData(imageURL, isPrimary, smallImageURL)
        }

        return retVal
    }
}
// endregion

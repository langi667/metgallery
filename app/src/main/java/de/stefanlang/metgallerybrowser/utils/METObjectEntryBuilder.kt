package de.stefanlang.metgallerybrowser.utils

import android.content.Context
import android.content.res.Resources
import de.stefanlang.core.models.HyperLink
import de.stefanlang.core.utils.Empty.allNullOrBlank
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.models.METObject
import javax.inject.Inject

/**
 * Wrapper around the METObject to prepare and provide UI representations of METObject properties
 */

data class METObjectEntryBuilder @Inject constructor(val appContext: Context) {

    // region Types

    data class Entry(
        val name: String,
        val value: String,
        val hyperlinks: List<HyperLink> = emptyList()
    )

    // endregion

    // region Properties

    private val noEntryString: String by lazy {
        resources.getString(R.string.no_value)
    }

    private val resources: Resources
        get() = appContext.resources

    // endregion

    // region Public API

    fun buildFor(metObject: METObject): List<Entry> {
        val retVal = createEntries(metObject)
        return retVal
    }

    // endregion

    // region Private API

    private fun createEntries(metObject: METObject): List<Entry> {
        val retVal = mutableListOf<Entry>()

        createAccessionEntry(metObject)?.let { entry -> retVal.add(entry) }
        createDepartmentEntry(metObject)?.let { entry -> retVal.add(entry) }
        createCultureEntry(metObject)?.let { entry -> retVal.add(entry) }

        createObjectDetailsEntry(metObject)?.let { entry -> retVal.add(entry) }
        createArtistEntry(metObject)?.let { entry -> retVal.add(entry) }
        createLocationEntry(metObject)?.let { entry -> retVal.add(entry) }

        createMiscEntry(metObject)?.let { entry -> retVal.add(entry) }

        return retVal
    }

    private fun createDepartmentEntry(metObject: METObject): Entry? {
        val entryValue = metObject.department
        val entryTitle = resources.getString(R.string.department_title)

        val retVal: Entry? = if (entryValue == null || entryValue.isBlank()) {
            null
        } else {
            Entry(entryTitle, entryValue)
        }

        return retVal
    }

    private fun createAccessionEntry(metObject: METObject): Entry? {
        val accessionNumber = metObject.accessionNumber
        val accessionYear = metObject.accessionYear
        val isPublicDomain = metObject.isPublicDomain

        val retVal: Entry? = if (allNullOrBlank(accessionYear, accessionYear, isPublicDomain)) {
            null
        } else {
            val content = resources.getString(
                R.string.accession_text,
                stringOrEmpty(accessionNumber),
                stringOrEmpty(accessionYear),
                boolOrEmpty(isPublicDomain)
            )
            val entry = Entry(resources.getString(R.string.accession_title), content)
            entry
        }

        return retVal
    }

    private fun createCultureEntry(metObject: METObject): Entry? {
        val culture: String? = metObject.culture
        val period: String? = metObject.period
        val dynasty: String? = metObject.dynasty

        val reign: String? = metObject.reign

        val retVal: Entry? = if (allNullOrBlank(culture, period, dynasty, reign)) {
            null
        } else {
            val content = resources.getString(
                R.string.culture_text,
                stringOrEmpty(culture),
                stringOrEmpty(period),
                stringOrEmpty(dynasty)
            )
            val entry = Entry(resources.getString(R.string.culture_title), content)
            entry
        }

        return retVal
    }

    private fun createObjectDetailsEntry(metObject: METObject): Entry? {
        val objectName: String? = metObject.objectName
        val objectEndDate: Int? = metObject.objectEndDate
        val objectDate: String? = metObject.objectDate

        val objectBeginDate: Int? = metObject.objectBeginDate
        val objectURL: String? = metObject.objectURL
        val objectWikidataURL: String? = metObject.objectWikidataURL

        val isTimelineWork: Boolean? = metObject.isTimelineWork
        val galleryNumber: String? = metObject.galleryNumber
        val portfolio: String? = metObject.portfolio

        val medium: String? = metObject.medium
        val dimensions: String? = metObject.dimensions
        val classification: String? = metObject.classification

        val retVal: Entry? = if (allNullOrBlank(
                objectName,
                objectEndDate,
                objectDate,
                objectBeginDate,
                objectURL,
                objectWikidataURL,
                isTimelineWork,
                galleryNumber,
                portfolio,
                medium,
                dimensions,
                classification
            )
        ) {
            null
        } else {
            val content = resources.getString(
                R.string.object_details_text,
                stringOrEmpty(objectName),
                stringOrEmpty(objectDate),
                intOrEmpty(objectBeginDate),
                intOrEmpty(objectEndDate),
                stringOrEmpty(objectURL),
                stringOrEmpty(objectWikidataURL),
                boolOrEmpty(isTimelineWork),
                stringOrEmpty(galleryNumber),
                stringOrEmpty(portfolio),
                stringOrEmpty(medium),
                stringOrEmpty(dimensions),
                stringOrEmpty(classification)
            )

            val hyperlinks = HyperLink.createList(objectURL, objectWikidataURL)
            val entry =
                Entry(resources.getString(R.string.object_details_title), content, hyperlinks)

            entry
        }

        return retVal
    }

    private fun createArtistEntry(metObject: METObject): Entry? {
        val artistRole: String? = metObject.artistRole
        val artistDisplayName: String? = metObject.artistDisplayName
        val artistDisplayBio: String? = metObject.artistDisplayBio

        val artistNationality: String? = metObject.artistNationality
        val artistBeginDate: String? = metObject.artistBeginDate
        val artistEndDate: String? = metObject.artistEndDate

        val artistGender: String? = metObject.artistGender
        val artistWikidataURL: String? = metObject.artistWikidataURL
        val artistULANURL: String? = metObject.artistULANURL

        val retVal: Entry? = if (allNullOrBlank(
                artistRole,
                artistDisplayName,
                artistDisplayBio,
                artistNationality,
                artistBeginDate,
                artistEndDate,
                artistGender,
                artistWikidataURL,
                artistULANURL
            )
        ) {
            null
        } else {
            val content = resources.getString(
                R.string.artist_text,
                stringOrEmpty(artistDisplayName),
                stringOrEmpty(artistRole),
                stringOrEmpty(artistGender),
                stringOrEmpty(artistNationality),
                stringOrEmpty(artistBeginDate),
                stringOrEmpty(artistEndDate),
                stringOrEmpty(artistDisplayBio),
                stringOrEmpty(artistWikidataURL),
                stringOrEmpty(artistULANURL)
            )
            val hyperlinks = HyperLink.createList(artistWikidataURL, artistULANURL)
            val entry = Entry(resources.getString(R.string.artist_title), content, hyperlinks)

            entry
        }

        return retVal
    }

    private fun createLocationEntry(metObject: METObject): Entry? {
        val geographyType: String? = metObject.geographyType
        val city: String? = metObject.city
        val state: String? = metObject.state

        val county: String? = metObject.county
        val region: String? = metObject.region
        val subregion: String? = metObject.subregion

        val locale: String? = metObject.locale
        val locus: String? = metObject.locus
        val excavation: String? = metObject.excavation

        val river: String? = metObject.river

        val retVal: Entry? = if (allNullOrBlank(
                geographyType,
                city,
                state,
                county,
                region,
                subregion,
                locale,
                locus,
                excavation,
                river
            )
        ) {
            null
        } else {
            val content = resources.getString(
                R.string.geography_text,
                stringOrEmpty(stringOrEmpty(geographyType)),
                stringOrEmpty(stringOrEmpty(excavation)),
                stringOrEmpty(stringOrEmpty(city)),
                stringOrEmpty(stringOrEmpty(county)),
                stringOrEmpty(stringOrEmpty(state)),
                stringOrEmpty(stringOrEmpty(subregion)),
                stringOrEmpty(stringOrEmpty(region)),
                stringOrEmpty(stringOrEmpty(locale)),
                stringOrEmpty(stringOrEmpty(locus))
            )
            val entry = Entry(resources.getString(R.string.geography_title), content)

            entry
        }

        return retVal
    }

    private fun createMiscEntry(metObject: METObject): Entry? {
        val creditLine: String? = metObject.creditLine
        val rightsAndReproduction: String? = metObject.rightsAndReproduction
        val linkResource: String? = metObject.linkResource

        val repository: String? = metObject.county
        val retVal: Entry? = if (allNullOrBlank(
                creditLine,
                rightsAndReproduction,
                linkResource,
                repository
            )
        ) {
            null
        } else {
            val content = resources.getString(
                R.string.misc_text,
                stringOrEmpty(stringOrEmpty(creditLine)),
                stringOrEmpty(stringOrEmpty(rightsAndReproduction)),
                stringOrEmpty(stringOrEmpty(linkResource)),
                stringOrEmpty(stringOrEmpty(repository))
            )
            val hyperlinks = HyperLink.createList(linkResource)
            val entry = Entry(resources.getString(R.string.misc_title), content, hyperlinks)

            entry
        }

        return retVal
    }

    // endregion

    private fun stringOrEmpty(string: String?): String {
        val retVal = if (string.isNullOrBlank()) {
            noEntryString
        } else {
            string
        }

        return retVal
    }

    private fun intOrEmpty(int: Int?): String {
        val retVal = int?.toString() ?: noEntryString
        return retVal
    }

    private fun boolOrEmpty(boolean: Boolean?): String {
        val retVal = when (boolean) {
            true -> {
                resources.getString(R.string.common_yes)
            }
            false -> {
                resources.getString(R.string.common_no)
            }
            else -> {
                noEntryString
            }
        }

        return retVal
    }

    // endregion
}



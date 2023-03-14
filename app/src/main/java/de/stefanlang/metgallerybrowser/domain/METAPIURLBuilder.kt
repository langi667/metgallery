package de.stefanlang.metgallerybrowser.domain

import android.net.Uri

object METAPIURLBuilder {

    // region Properties

    private const val authority = "collectionapi.metmuseum.org";
    private const val version = "v1";

    // endregion

    // region Public API

    fun objectsSearchURL(query: String): String {
        val builder = prepareURLBuilder()
        builder.appendPath("search")

        val queryWrapped = wrapSearchQuery(query)
        builder.appendQueryParameter("q", queryWrapped)
        val retVal = builder.build().toString()

        return retVal
    }

    fun objectURL(objectID: Int): String {
        val builder = prepareURLBuilder()
        builder.appendPath("objects")
        builder.appendPath("$objectID")

        val retVal = builder.build().toString()
        return retVal
    }

    fun smallImageURLForImageURL(url: String): String {
        val retVal = url.replace("/original/", "/web-large/")
        return retVal
    }

    // endregion

    // region Private API

    private fun prepareURLBuilder(): Uri.Builder {
        val retVal = Uri.Builder()

        retVal.scheme("https")
            .authority(authority)
            .appendPath("public")
            .appendPath("collection")
            .appendPath(version)

        return retVal
    }

    private fun wrapSearchQuery(query: String): String {
        var retVal = query
        val quotation = '\"'

        if (!retVal.startsWith(quotation)) {
            retVal = "$quotation$retVal"
        }

        if (!retVal.endsWith(quotation)) {
            retVal = "$retVal$quotation"
        }

        return retVal
    }

    // endregion
}
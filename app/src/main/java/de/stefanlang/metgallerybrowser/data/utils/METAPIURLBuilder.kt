package de.stefanlang.metgallerybrowser.data.utils

import android.net.Uri
import java.net.URLEncoder

object METAPIURLBuilder {
    private const val authority = "collectionapi.metmuseum.org";
    private const val version = "v1";

    fun objectsSearchURL(query: String): String {
        val builder = prepareURLBuilder()
        builder.appendPath("search")
        val queryEncoded = toHTMLEncodedString(query)
        builder.appendQueryParameter("q", queryEncoded)

        val retVal = builder.build().toString()
        return retVal
    }

    private fun prepareURLBuilder(): Uri.Builder {
        val retVal = Uri.Builder()

        retVal.scheme("https")
            .authority(authority)
            .appendPath("public")
            .appendPath("collection")
            .appendPath(version)

        return retVal
    }

    private fun toHTMLEncodedString(string: String): String {
        val retVal = URLEncoder.encode(string, "utf-8")
        return retVal
    }

}
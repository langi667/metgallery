package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder

// TODO: Singleton
object ObjectsSearchRepository {

    fun search(query: String) {
        val url = METAPIURLBuilder.objectsSearchURL("Africa");
        // NetworkAPI.get(url)
    }
}
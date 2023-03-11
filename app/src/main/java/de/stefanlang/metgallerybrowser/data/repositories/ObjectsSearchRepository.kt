package de.stefanlang.metgallerybrowser.data.repositories

import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearchResult
import de.stefanlang.metgallerybrowser.data.utils.JSONParser
import de.stefanlang.metgallerybrowser.data.utils.METAPIURLBuilder
import de.stefanlang.network.NetworkAPI

// TODO: Singleton
object ObjectsSearchRepository {

    // region Public API

    suspend fun search(query: String): Result<ObjectsSearch> {
        val url = METAPIURLBuilder.objectsSearchURL(query);
        val result = NetworkAPI.get(url)

        val response = result.getOrElse { error ->
            return Result.failure(error)
        }

        val searchResultRaw = JSONParser.mapObjectFrom<ObjectsSearchResult>(response.data)
        val searchResult = ObjectsSearch(query, searchResultRaw)
        val retVal = Result.success(searchResult)

        return retVal
    }

    // endregion
}
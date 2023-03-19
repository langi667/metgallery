package de.stefanlang.metgallerybrowser.objectdetail.api

import de.stefanlang.metgallerybrowser.objectdetail.model.METObject

interface ObjectDetailAPI {
    suspend fun objectForID(objectID: Int): Result<METObject>
}
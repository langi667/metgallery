package de.stefanlang.feature.objectdetail.api

import de.stefanlang.feature.objectdetail.model.METObject


interface ObjectDetailAPI {
    suspend fun objectForID(objectID: Int): Result<METObject>
}
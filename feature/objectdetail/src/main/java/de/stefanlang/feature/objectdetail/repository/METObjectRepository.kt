package de.stefanlang.feature.objectdetail.repository

import de.stefanlang.feature.objectdetail.model.METObject


// TODO: maybe remove MET
interface METObjectRepository {
    suspend fun fetchObjectForResult(id: Int): Result<METObject>?
}
package de.stefanlang.metgallerybrowser.objectdetail.repository

import de.stefanlang.metgallerybrowser.objectdetail.model.METObject

// TODO: maybe remove MET
interface METObjectRepository {
    suspend fun fetchObjectForResult(id: Int): Result<METObject>?
}
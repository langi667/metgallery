package de.stefanlang.metgallerybrowser.repository.metobject

import de.stefanlang.metgallerybrowser.models.METObject

// TODO: maybe remove MET
interface METObjectRepository {
    suspend fun fetchObjectForResult(id: Int): Result<METObject>?
}
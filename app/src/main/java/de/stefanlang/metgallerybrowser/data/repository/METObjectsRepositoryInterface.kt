package de.stefanlang.metgallerybrowser.data.repository

import de.stefanlang.metgallerybrowser.data.models.METObject

// TODO: maybe remove MET
interface METObjectRepositoryInterface {
    suspend fun fetchObjectForResult(id: Int): Result<METObject>?
}
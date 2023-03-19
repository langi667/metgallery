package de.stefanlang.metgallerybrowser.objectdetail.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.metgallerybrowser.objectdetail.api.ObjectDetailAPI
import de.stefanlang.metgallerybrowser.objectdetail.api.ObjectDetailAPIImpl
import de.stefanlang.metgallerybrowser.objectdetail.repository.METObjectRepository
import de.stefanlang.metgallerybrowser.objectdetail.repository.METObjectRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObjectDetailModule {

    @Provides
    @Singleton
    fun provideObjectsRepository(api: ObjectDetailAPI): METObjectRepository {
        return METObjectRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideObjectDetailAPI(networkAPI: NetworkAPI): ObjectDetailAPI {
        return ObjectDetailAPIImpl(networkAPI)
    }
}
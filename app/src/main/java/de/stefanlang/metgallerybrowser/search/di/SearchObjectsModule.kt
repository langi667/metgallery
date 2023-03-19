package de.stefanlang.metgallerybrowser.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.metgallerybrowser.search.api.ObjectsSearchAPI
import de.stefanlang.metgallerybrowser.search.api.ObjectsSearchAPIImpl
import de.stefanlang.metgallerybrowser.search.repository.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.search.repository.METObjectsSearchRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchObjectsModule {

    @Provides
    @Singleton
    fun provideObjectsSearchRepository(api: ObjectsSearchAPI): METObjectsSearchRepository {
        return METObjectsSearchRepositoryImpl(api)
    }


    @Provides
    @Singleton
    fun provideSearchAPI(networkAPI: NetworkAPI): ObjectsSearchAPI {
        return ObjectsSearchAPIImpl(networkAPI)
    }
}
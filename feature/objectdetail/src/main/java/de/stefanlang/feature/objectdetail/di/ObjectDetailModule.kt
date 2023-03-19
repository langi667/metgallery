package de.stefanlang.feature.objectdetail.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.feature.objectdetail.repository.METObjectRepository
import de.stefanlang.feature.objectdetail.repository.METObjectRepositoryImpl

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObjectDetailModule {

    @Provides
    @Singleton
    fun provideObjectsRepository(api: de.stefanlang.feature.objectdetail.api.ObjectDetailAPI): METObjectRepository {
        return METObjectRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideObjectDetailAPI(networkAPI: NetworkAPI): de.stefanlang.feature.objectdetail.api.ObjectDetailAPI {
        return de.stefanlang.feature.objectdetail.api.ObjectDetailAPIImpl(networkAPI)
    }

    @Provides
    @Singleton
    fun provideMETObjectEntryBuilder(appContext: Application): de.stefanlang.feature.objectdetail.model.METObjectEntryBuilder {
        return de.stefanlang.feature.objectdetail.model.METObjectEntryBuilder(appContext)
    }
}
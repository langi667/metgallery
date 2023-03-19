package de.stefanlang.core.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageAPIModule {
    @Provides
    @Singleton
    fun provideImageAPI(networkAPI: NetworkAPI): ImageAPI {
        return ImageAPIImpl(networkAPI)
    }


}
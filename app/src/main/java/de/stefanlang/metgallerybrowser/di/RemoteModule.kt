package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import de.stefanlang.metgallerybrowser.api.METAPI
import de.stefanlang.metgallerybrowser.api.METAPIImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideMETAPI(networkAPI: NetworkAPI): METAPI {
        return METAPIImpl(networkAPI)
    }
}
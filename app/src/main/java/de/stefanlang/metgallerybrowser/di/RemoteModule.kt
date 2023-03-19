package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.domain.remote.METAPIImpl
import de.stefanlang.network.NetworkAPI
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
package de.stefanlang.core.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.stefanlang.core.network.NetworkAPI
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkAPI(
        executor: Executor,
        idProvider: AtomicInteger,
        cronetEngine: CronetEngine
    ): NetworkAPI {
        val retVal = NetworkAPI(executor, idProvider, cronetEngine)
        return retVal
    }

    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        val retVal = Executors.newWorkStealingPool()
        return retVal
    }

    @Provides
    @Singleton
    fun provideIDProvider() = AtomicInteger(0)

    @Provides
    @Singleton
    fun provideCronetEngine(@ApplicationContext appContext: Context): CronetEngine {
        val retVal = CronetEngine.Builder(appContext).build()
        return retVal
    }
}
package de.stefanlang.metgallerybrowser.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stefanlang.metgallerybrowser.utils.METObjectEntryBuilder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideMETObjectEntryBuilder(appContext: Application): METObjectEntryBuilder {
        return METObjectEntryBuilder(appContext)
    }
}
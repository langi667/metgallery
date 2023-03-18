package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.data.repository.ImageRepository
import de.stefanlang.metgallerybrowser.data.repository.METObjectRepository
import de.stefanlang.metgallerybrowser.data.repository.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.domain.repository.ImageRepositoryImpl
import de.stefanlang.metgallerybrowser.domain.repository.METObjectRepositoryImpl
import de.stefanlang.metgallerybrowser.domain.repository.METObjectsSearchRepositoryImpl
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideImageRepository(
        @Named("MaxImagesDefault") maxImages: Int,
        api: METAPI
    ): ImageRepository {
        return ImageRepositoryImpl(maxImages, api)
    }

    @Provides
    @ViewModelScoped
    fun provideObjectsSearchRepository(api: METAPI): METObjectsSearchRepository {
        return METObjectsSearchRepositoryImpl(api)
    }

    @Provides
    @ViewModelScoped
    fun provideObjectsRepository(api: METAPI): METObjectRepository {
        return METObjectRepositoryImpl(api)
    }

    @Provides
    @ViewModelScoped
    @Named("MaxImagesDefault")
    fun maxImagesDefault() = 15

}
package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.stefanlang.metgallerybrowser.api.METAPI
import de.stefanlang.metgallerybrowser.repository.image.ImageRepository
import de.stefanlang.metgallerybrowser.repository.image.ImageRepositoryImpl
import de.stefanlang.metgallerybrowser.repository.metobject.METObjectRepository
import de.stefanlang.metgallerybrowser.repository.metobject.METObjectRepositoryImpl
import de.stefanlang.metgallerybrowser.repository.metobject.METObjectsSearchRepository
import de.stefanlang.metgallerybrowser.repository.metobject.METObjectsSearchRepositoryImpl
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
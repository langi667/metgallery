package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.stefanlang.metgallerybrowser.repository.image.ImageRepository
import de.stefanlang.metgallerybrowser.repository.image.ImageRepositoryImpl
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideImageRepository(
        @Named("MaxImagesDefault") maxImages: Int,
        api: de.stefanlang.core.api.ImageAPI
    ): ImageRepository {
        return ImageRepositoryImpl(maxImages, api)
    }


    @Provides
    @ViewModelScoped
    @Named("MaxImagesDefault")
    fun maxImagesDefault() = 15

}
package de.stefanlang.core.domain.image

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object ImageModule {

    @Provides
    @ViewModelScoped
    fun provideImageRepository(
        @Named("MaxImagesDefault") maxImages: Int,
        api: ImageAPI
    ): ImageRepository {
        return ImageRepositoryImpl(maxImages, api)
    }

    @Provides
    @ViewModelScoped
    @Named("MaxImagesDefault")
    fun maxImagesDefault() = 15

}
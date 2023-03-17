package de.stefanlang.metgallerybrowser.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.stefanlang.metgallerybrowser.data.repository.ImageRepositoryInterface
import de.stefanlang.metgallerybrowser.data.repository.METObjectRepositoryInterface
import de.stefanlang.metgallerybrowser.data.repository.METObjectsSearchRepositoryInterface
import de.stefanlang.metgallerybrowser.domain.repository.ImageRepository
import de.stefanlang.metgallerybrowser.domain.repository.METObjectRepository
import de.stefanlang.metgallerybrowser.domain.repository.METObjectsSearchRepository
import javax.inject.Named


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideImageRepository(@Named("MaxImagesDefault") maxImages: Int): ImageRepositoryInterface {
        return ImageRepository(maxImages)
    }

    @Provides
    @ViewModelScoped
    fun provideObjectsSearchRepository(): METObjectsSearchRepositoryInterface {
        return METObjectsSearchRepository()
    }

    @Provides
    @ViewModelScoped
    fun provideObjectsRepository(): METObjectRepositoryInterface {
        return METObjectRepository()
    }

    @Provides
    @ViewModelScoped
    @Named("MaxImagesDefault")
    fun maxImagesDefault() = 15

}
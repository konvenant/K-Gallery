package com.example.k_gallery.di

import android.content.Context
import com.example.k_gallery.data.dataSources.api.RetrofitInstance
import com.example.k_gallery.data.dataSources.local.AllVideosSource
import com.example.k_gallery.data.dataSources.local.FavoriteDataSource
import com.example.k_gallery.data.dataSources.local.FolderDataSource
import com.example.k_gallery.data.dataSources.local.ImageDataSource
import com.example.k_gallery.data.dataSources.local.PhotosSource
import com.example.k_gallery.data.dataSources.local.VideoFolderDataSource
import com.example.k_gallery.data.dataSources.local.VideosInFolderDataSource
import com.example.k_gallery.data.repositories.localRepositories.AllVideosRepository
import com.example.k_gallery.data.repositories.localRepositories.FavoriteRepository
import com.example.k_gallery.data.repositories.localRepositories.FolderRepository
import com.example.k_gallery.data.repositories.localRepositories.GalleryRepository
import com.example.k_gallery.data.repositories.localRepositories.ImageRepository
import com.example.k_gallery.data.repositories.localRepositories.PhotosRepository
import com.example.k_gallery.data.repositories.localRepositories.VideoFoldersRepository
import com.example.k_gallery.data.repositories.localRepositories.VideosInFolderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAgroRepository(
    ) = GalleryRepository()

    @Singleton
    @Provides
    fun provideFolderDataSource(
        @ApplicationContext context: Context,
        imageDataSource: ImageDataSource
    ) = FolderDataSource(context)

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context

    @Singleton
    @Provides
    fun provideFolderRepository(
        folderDataSource: FolderDataSource
    ) = FolderRepository(folderDataSource)

    @Singleton
    @Provides
    fun provideImageRepository(
        imageDataSource: ImageDataSource
    ) = ImageRepository(imageDataSource)

    @Singleton
    @Provides
    fun provideImageDataSource(
        @ApplicationContext context: Context
    ) = ImageDataSource(context)

    @Singleton
    @Provides
    fun providePhotosRepository(
        photosSource: PhotosSource
    ) = PhotosRepository(photosSource)

    @Singleton
    @Provides
    fun providePhotosSource(
        @ApplicationContext context: Context
    ) = PhotosSource(context)

    @Singleton
    @Provides
    fun provideVideoFoldersSource(
        @ApplicationContext context: Context
    ) = VideoFolderDataSource(context)


    @Singleton
    @Provides
    fun provideVideoFoldersRepository(
        videoFolderDataSource: VideoFolderDataSource
    ) = VideoFoldersRepository(videoFolderDataSource)


    @Singleton
    @Provides
    fun provideVideosInFoldersDataSource(
        @ApplicationContext context: Context
    ) = VideosInFolderDataSource(context)


    @Singleton
    @Provides
    fun provideVideoInFoldersRepository(
        videosInFolderDataSource: VideosInFolderDataSource
    ) = VideosInFolderRepository(videosInFolderDataSource)



    @Singleton
    @Provides
    fun provideAllVideosDataSource(
        @ApplicationContext context: Context
    ) = AllVideosSource(context)


    @Singleton
    @Provides
    fun provideAllVideosRepository(
        allVideosSource: AllVideosSource
    ) = AllVideosRepository(allVideosSource)

    @Singleton
    @Provides
    fun provideFavoriteDataSource(
        @ApplicationContext context: Context
    ) = FavoriteDataSource(context)


    @Singleton
    @Provides
    fun provideFavoriteRepository(
        favoriteDataSource : FavoriteDataSource
    ) = FavoriteRepository(favoriteDataSource)


    @Singleton
    @Provides
    fun provideUserRetrofitInstance(

    ) = RetrofitInstance.api


}
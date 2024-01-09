package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.dataSources.local.PhotosSource
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    private val photosSource: PhotosSource
) {
    suspend fun getAllImages() : List<Image> {
        return photosSource.getAllImages()
    }
}
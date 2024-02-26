package com.example.k_gallery.data.repositories.localRepositories

import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.dataSources.local.ImageDataSource
import javax.inject.Inject


class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {
    suspend fun getImagesInFolder(folderId: String): List<Image> {
        return imageDataSource.getImagesInFolder(folderId)
    }
}
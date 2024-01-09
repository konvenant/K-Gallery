package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.data.dataSources.local.FolderDataSource
import javax.inject.Inject

class FolderRepository @Inject constructor(private val folderDataSource: FolderDataSource) {
    suspend fun getFolderWithRecentImages() : List<Folder> {
        return folderDataSource.getFolderWithRecentImage()
    }
}
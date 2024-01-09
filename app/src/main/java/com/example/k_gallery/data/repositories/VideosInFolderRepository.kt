package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.data.dataSources.local.VideosInFolderDataSource
import javax.inject.Inject

class VideosInFolderRepository @Inject constructor(
    private val videosInFolderDataSource: VideosInFolderDataSource
) {
    suspend fun getVideosInFolder(folderId: String) : List<Video>{
        return videosInFolderDataSource.getVideosInFolder(folderId)
    }
}
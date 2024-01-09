package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.VideoFolder
import com.example.k_gallery.data.dataSources.local.VideoFolderDataSource
import javax.inject.Inject

class VideoFoldersRepository @Inject constructor(
   private val videoFolderDataSource: VideoFolderDataSource
) {
    suspend fun getVideoFoldersWithRecentVideo():List<VideoFolder> {
        return videoFolderDataSource.getVideoFolderWithRecentVideo()
    }

    fun getTestTest() : String {
        return videoFolderDataSource.getTestTest()
    }
}
package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.AllVideosSource
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.data.dataSources.local.VideosDataSource
import javax.inject.Inject

class AllVideosRepository @Inject constructor(
    private val allVideosDataSource: AllVideosSource
){
    suspend fun getAllVideos(): List<Video> {
        return allVideosDataSource.getAllVideos()
    }
}
package com.example.k_gallery.data.dataSources.local


data class VideoFolder(
    val id: Long,
    val name: String? = null,
    val descVideoUri: String,
    val videoCount: Int? = null,
    val folderSize: String? = null
)

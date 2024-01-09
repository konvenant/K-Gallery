package com.example.k_gallery.data.dataSources.local

import android.net.Uri

data class Folder(
    val folderId: Int,
    val folderName: String,
    val recentImagePath: String,
    val folderCount: Int? = null,
    val folderSize: String? = null
)

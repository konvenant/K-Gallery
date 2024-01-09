package com.example.k_gallery.data.dataSources.local

import android.net.Uri


data class Video(
    val id: Long,
    val name: String,
    val uri: String,
    val dateTaken: Long,
    val size: Long,
    val videoUri: Uri
)

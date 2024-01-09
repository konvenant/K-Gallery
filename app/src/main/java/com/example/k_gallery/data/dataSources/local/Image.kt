package com.example.k_gallery.data.dataSources.local

import android.net.Uri


data class Image(
    val id: Long,
    val name: String,
    val path: String,
    val dateTaken: Long,
    val uri: Uri,
    val size: Long
)

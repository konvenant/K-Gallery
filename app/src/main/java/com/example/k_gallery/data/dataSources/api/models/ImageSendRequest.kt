package com.example.k_gallery.data.dataSources.api.models

data class ImageSendRequest(
    val images: List<String>,
    val fromEmail: String,
    val toEmail: String,
    val caption: String
)

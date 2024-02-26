package com.example.k_gallery.data.dataSources.api.models

data class ImageDeleteRequest(
    val ids: List<String>,
    val email: String
)

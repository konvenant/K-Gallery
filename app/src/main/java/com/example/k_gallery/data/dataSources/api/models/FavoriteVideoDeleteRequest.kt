package com.example.k_gallery.data.dataSources.api.models

data class FavoriteVideoDeleteRequest(
    val email: String,
    val arrayOfId: List<String>
)
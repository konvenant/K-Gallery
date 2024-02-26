package com.example.k_gallery.data.dataSources.api.models

data class FavoriteImageDeleteRequest(
    val email: String,
    val arrayOfId: List<String>
)
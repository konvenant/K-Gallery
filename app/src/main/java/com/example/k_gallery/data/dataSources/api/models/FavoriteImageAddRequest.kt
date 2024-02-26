package com.example.k_gallery.data.dataSources.api.models

data class FavoriteImageAddRequest(
    val favoriteImages: List<String>,
    val email:String
)
package com.example.k_gallery.data.dataSources.api.models

data class FavoriteVideoAddRequest(
    val favoriteVideos: List<String>,
    val email:String
)
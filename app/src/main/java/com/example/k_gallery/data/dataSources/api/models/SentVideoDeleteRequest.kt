package com.example.k_gallery.data.dataSources.api.models


data class SentVideoDeleteRequest(
    val email: String,
    val arrayOfObjects: List<VideoObject>
)

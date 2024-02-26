package com.example.k_gallery.data.dataSources.api.models

data class Notice(
    val __v: Int,
    val _id: String,
    val action: String,
    val body: String,
    val date: String,
    val email: String,
    val heading: String,
    val isRead: Boolean,
    val url: String
)
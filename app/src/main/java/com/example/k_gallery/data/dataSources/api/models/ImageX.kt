package com.example.k_gallery.data.dataSources.api.models

data class ImageX(
    val __v: Int,
    val _id: String,
    val caption: String,
    val date: String,
    val dateRecieverDelete: String,
    val dateSenderDelete: String,
    val delivered: Boolean,
    val fromEmail: String,
    val imageUrl: String,
    val isRecieverDeleted: Boolean,
    val isSenderDeleted: Boolean,
    val read: Boolean,
    val toEmail: String
)
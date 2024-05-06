package com.example.k_gallery.data.dataSources.api.models

data class ChatMessage(
    val sender: String,
    val caption: String,
    val url: String,
    val timeStamp: String,
    val isFromMe: Boolean,
    val isVideo: Boolean,
    val isImage: Boolean,
    val isSenderDeleted: Boolean,
    val isReceiverDeleted: Boolean,
    val delivered: Boolean,
    val read: Boolean
)

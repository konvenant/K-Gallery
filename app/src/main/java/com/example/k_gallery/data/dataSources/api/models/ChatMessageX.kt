package com.example.k_gallery.data.dataSources.api.models

import java.util.UUID

data class ChatMessageX(
    val caption: String,
    val dateRecieverDelete: String,
    val dateSenderDelete: String,
    val delivered: Boolean,
    val isFromMe: Boolean,
    val isImage: Boolean,
    val isReceiverDeleted: Boolean,
    val isSenderDeleted: Boolean,
    val isVideo: Boolean,
    val read: Boolean,
    val sender: String,
    val timeStamp: String,
    val url: String,
    val id: String = UUID.randomUUID().toString()
)
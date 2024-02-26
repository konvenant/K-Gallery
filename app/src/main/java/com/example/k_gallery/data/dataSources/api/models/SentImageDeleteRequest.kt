package com.example.k_gallery.data.dataSources.api.models

import android.provider.ContactsContract.CommonDataKinds.Email

data class SentImageDeleteRequest(
    val email: String,
    val arrayOfobjects: List<ImageObject>
)

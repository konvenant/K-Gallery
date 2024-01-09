package com.example.k_gallery.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class IntentViewModel :ViewModel() {
    var uri: Uri? by mutableStateOf(null)
    private set

    fun updateUri(uri: Uri?) {
        this.uri = uri
    }
}
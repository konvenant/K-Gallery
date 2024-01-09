package com.example.k_gallery.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.k_gallery.presentation.util.DisplayImageOrVideo

class DisplayActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        setContent {
            if (uri != null) {
                DisplayImageOrVideo(uri = uri)
            }
        }
    }
}
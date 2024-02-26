package com.example.k_gallery.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.k_gallery.presentation.graph.MainScreen
import com.example.k_gallery.presentation.util.DisplayImage
import com.example.k_gallery.presentation.util.DisplayVideo
import com.example.k_gallery.presentation.viewmodel.IntentViewModel
import com.example.k_gallery.ui.theme.KGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val intentViewModel by viewModels<IntentViewModel>()
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KGalleryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (intent?.action) {
                        Intent.ACTION_SEND -> {
                            if (intent.type?.startsWith("image/") == true) {
                                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                    DisplayImage(uri = it)
                                }
                            } else if(intent.type?.startsWith("video/") == true) {
                                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                   DisplayVideo(uri = it)
                                }
                            }
                        }
                        Intent.ACTION_SEND_MULTIPLE -> {

                        }
                        else -> {
                            MainScreen(this)
                        }
                    }
//          intentViewModel.uri?.let {
//              Toast.makeText(this,"called: on not null", Toast.LENGTH_SHORT).show()
//            val displayIntent = Intent(this,DisplayActivity::class.java)
//              displayIntent.putExtra(Intent.EXTRA_STREAM, intentViewModel.uri)
//              startActivity(displayIntent)
//              finish()
//          } ?: run {
//              Toast.makeText(this,"called: on main", Toast.LENGTH_SHORT).show()
//              MainScreen()
//          }

                }
            }
        }
    }


//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
//        } else {
//            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
//        }
//        Toast.makeText(this,"called: $uri", Toast.LENGTH_SHORT).show()
//        intentViewModel.updateUri(uri)
//
//    }
}

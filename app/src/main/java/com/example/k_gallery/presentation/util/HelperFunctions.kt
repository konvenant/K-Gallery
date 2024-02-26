package com.example.k_gallery.presentation.util

import android.app.RecoverableSecurityException
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import coil.compose.AsyncImage
import com.example.k_gallery.presentation.screens.local.VideoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun calculateImageSize(size: Long) : String{
    val kbSize = size / 1024.0
    val mbSize = kbSize / 1024.0
    val gbSize = mbSize / 1024.0


    val kb = String.format("%.2f", kbSize)
    val mb = String.format("%.2f", mbSize)
    val gb = String.format("%.2f", gbSize)

    return when {
        gbSize >= 1.0 -> "$gb GB"
        mbSize >= 1.0 -> "$mb MB"
        else -> "$kb KB"
    }
}


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun SetImmersiveMode(active: Boolean) {
    val windowInsetsController = LocalView.current.windowInsetsController

    if (active) {
        windowInsetsController?.hide(WindowInsets.Type.systemBars())
        windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else{
        windowInsetsController?.show(WindowInsets.Type.systemBars())
    }
}

fun deleteImage(imageUri: String,context: Context): Boolean {
    val contentResolver = context.contentResolver
    val imgUri: Uri = Uri.parse("file:/$imageUri")

    return try {
        contentResolver.delete(
            imgUri,
            null,
            null
        )
        Log.d("Delete","Deleted")
        true
    } catch(e:Exception){
        Log.d("Delete","Not Deleted $e")
        false
    }
//    val deletedRows =  contentResolver.delete(
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//        null,
//        null
//    )
//
//    return deletedRows > 0

}

fun deleteVideo(videoUri: Uri,context: Context): Boolean {
    val contentResolver = context.contentResolver
    val selection = "${MediaStore.Video.Media._ID} = ?"
    val selectionArgs = arrayOf(videoUri.lastPathSegment)

    val deletedRows =  contentResolver.delete(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        selection,
        selectionArgs
    )

    return deletedRows > 0

}


suspend fun deletePhoto(photoUri:Uri,context:Context,intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>) {
    val contentResolver = context.contentResolver
    withContext(Dispatchers.IO) {
        try{
            contentResolver.delete(photoUri,null,null)
        } catch(e:SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(contentResolver, listOf(photoUri)).intentSender
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as RecoverableSecurityException
                    recoverableSecurityException.userAction.actionIntent.intentSender
                }
                else -> null
            }
            intentSender?.let { sender->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }
    }
}
@Composable
fun DisplayImageOrVideo(uri: Uri){
    val scale = remember {
        mutableFloatStateOf(1f)
    }
    val transformableState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (uri.toString().endsWith(".mp4")) {
            VideoPlayer(videoUri = uri) {
            }
        } else{
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(transformableState)
                    .graphicsLayer {
                        scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                        scaleY = maxOf(.5f, minOf(3f, scale.floatValue))

                    },

                )
        }
    }
}

@Composable
fun DisplayImage(uri: Uri){
    val scale = remember {
        mutableFloatStateOf(1f)
    }
    val transformableState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(transformableState)
                    .graphicsLayer {
                        scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                        scaleY = maxOf(.5f, minOf(3f, scale.floatValue))

                    },

                )

    }
}

@Composable
fun DisplayVideo(uri: Uri){

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

            VideoPlayer(videoUri = uri) {
            }

        }
    }

data class BottomNavigationItem(
    val title:String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val hasNew: Boolean,
    val badgeCount: Int? = null,
    val route: String
)

package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.WindowInsetsController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavController
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.presentation.screens.local.OnlineVideoPlayer
import com.example.k_gallery.presentation.screens.local.shareVideo
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ViewOneVideoScreen(
    navController: NavController,
    videoUrl: String,
    desc: String,
    id: String,
    date: String,
    userViewModel: UserViewModel,
    sharedViewModel: UserSharedViewModel
) {
    val windowInsetsController = LocalView.current.windowInsetsController

    val context = LocalContext.current
    var isVisible by remember {
        mutableStateOf(true)
    }
    val scale = remember {
        mutableFloatStateOf(1f)
    }
    val rotationState = remember {
        mutableFloatStateOf(0f)
    }


    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom

    }


    var addResult by remember { mutableStateOf(false) }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val addFavoriteVideoResponse by userViewModel.addFavoriteVideo.observeAsState()

    var isIconBtnEnabled by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = isVisible){

        if (!isVisible) {
            windowInsetsController?.hide(WindowInsets.Type.systemBars())
            windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else{
            windowInsetsController?.show(WindowInsets.Type.systemBars())
        }
    }

    val imageModifier = if (isVisible){
        Modifier
            .fillMaxSize()
            .clickable {
                isVisible = !isVisible
            }
            .background(Color.Black)
            .graphicsLayer {
                if (!isVisible) {
                    scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                    scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
                }
            }
    } else{
        Modifier
            .fillMaxSize()
            .clickable {
                isVisible = !isVisible
//                                           navController.navigate(NavHelper.DetailsScreen.route + "/${folderId.toString()}")
            }
            .background(Color.Black)
            .transformable(zoomState)
            .graphicsLayer {
                if (!isVisible) {
                    scaleX = maxOf(.5f, minOf(3f, scale.floatValue))
                    scaleY = maxOf(.5f, minOf(3f, scale.floatValue))
                }
            }
    }

    var expanded by remember {
        mutableStateOf(false)
    }





    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

    }









    Scaffold(
        topBar = {
            if (isVisible) {
                TopAppBar(
                    title = {

                    },
                    actions = {
                        val email = sharedViewModel.userData.value?.user?.email
                        IconButton(
                            enabled = isIconBtnEnabled,
                            onClick = {
                                addResult = true
                                isIconBtnEnabled = false
                                userViewModel.addFavoriteVideo(email!!, videoUrl)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {expanded = false}
                        ) {

                            DropdownMenuItem(
                                text = { Text(text = "Info") },
                                onClick = {   navController.navigate(NavHelper.RemoteDetailsScreen.route+"/${desc}"+"/${date}"+"/${desc}")  }
                            )

                            var downloadStatus by remember {
                                mutableStateOf("")
                            }

                            DropdownMenuItem(

                                text = { Text(text = "Download") },

                                onClick = {
                                    if (videoUrl.isNotEmpty()){
                                        downloadVideo(videoUrl, context)
                                    } else{
                                        Toast.makeText(context,"wait for video to load",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                    )
                )

            }
        },
        bottomBar = {
            if (isVisible){
                BottomAppBar (
                    actions = {
                        IconButton(
                            onClick = {
                                shareOnlineVideo(videoUrl,context, shareLauncher){ show, msg ->
                                    if (show){
                                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }


                    }, containerColor = Color.Transparent
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
          OnlineVideoPlayer(videoUri = videoUrl) {

          }
        }

        if (addResult) {

            when (addFavoriteVideoResponse) {
                is Resource.Error -> {
                    isIconBtnEnabled = true
                    isLoading = false
                    val err = (addFavoriteVideoResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    addResult = false
                }

                is Resource.Success -> {
                    isIconBtnEnabled = false
                    isLoading = false
                    val success = (addFavoriteVideoResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    addResult = false
                }

                is Resource.Loading -> {
                    isLoading = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }



    }
}



 fun downloadVideo(
    url: String,
   context: Context
){
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Video_K-Gallery")
        .setDescription("Downloading.....")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)


    val dm = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

    dm.enqueue(request)
}



  @OptIn(DelicateCoroutinesApi::class)
  fun downloadVideoToShare(
    videoUrl: String,
    context: Context
 ) : File? {
      try {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
          val fileName = "shared_video.mp4"
          val file = File(directory,fileName)



          GlobalScope.launch(Dispatchers.IO) {
              val url = URL(videoUrl)
              val connection = url.openConnection() as HttpURLConnection
              connection.connect()

              val inputStream = connection.inputStream
              val fileOutputStream = FileOutputStream(file)
              val buffer = ByteArray(1024)
              var len = 0
              while (inputStream.read(buffer).also { len = it } > 0 ){
                  fileOutputStream.write(buffer,0,len)
              }
              fileOutputStream.close()
              inputStream.close()


          }

          return file
      } catch (e: Exception) {
          e.printStackTrace()
         return null
      }

}









fun downloadVideo(context: Context, videoUrl: String) {
    val request = DownloadManager.Request(Uri.parse(videoUrl))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        .setTitle("Video Downloaded")
        .setDescription("Downloading Video.....")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"video.mp4")


//    val downloadId = downloadManager.enqueue(request)
//
//    val imageUri = downloadManager.getUriForDownloadedFile(downloadId)
}


fun shareOnlineVideo(
    videoUrl: String,
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    isError: (Boolean,String) -> Unit,
){

    val filename = "downloaded_video.mp4"
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


    val request = DownloadManager.Request(Uri.parse(videoUrl))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        .setTitle("Video Downloaded")
        .setDescription("Downloading Video.....")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)

    val downloadId = downloadManager.enqueue(request)


    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadId2 = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (downloadId2 == downloadId){
                val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + filename
                shareVideo(context!!,filePath)
                context.unregisterReceiver(this)
            }
        }
    }

    context.registerReceiver(onComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


}
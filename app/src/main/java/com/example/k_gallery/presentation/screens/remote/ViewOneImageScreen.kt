package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.k_gallery.data.dataSources.api.models.Message
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.random.Random

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOneImageScreen(
    navController: NavController,
    imageUrl: String,
    desc: String,
    imageName: String,
    date: String,
    userViewModel:UserViewModel,
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

    val addFavoriteImageResponse by userViewModel.addFavoriteImage.observeAsState()


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


    val bitmapState = remember {
        mutableStateOf<Bitmap?>(null)
    }


    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

    }


    val scope = rememberCoroutineScope()






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
                                userViewModel.addFavoriteImage(email!!, imageUrl)
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

                                onClick = {   navController.navigate(NavHelper.RemoteDetailsScreen.route+"/${imageName}"+"/${date}"+"/${desc}")  }
                            )

                            var showMessage by remember {
                                mutableStateOf(false)
                            }
                            DropdownMenuItem(
                                text = { Text(text = "Download") },

                                onClick = {
                                    if (imageUrl.isNotEmpty()){
                                        downloadImage( imageUrl,context = context, onSuccess = { drawable ->
                                            //Convert drawable to bitmap
                                            val bitmap = drawable.toBitmap()
                                            //save the bitmap to device
                                            saveBitmapToDevice(context,bitmap,"$email-image.jpg")
                                            showMessage = true
                                        })

                                        if (showMessage){
                                            Toast.makeText(context,"image saved to pictures",Toast.LENGTH_SHORT).show()
                                        showMessage = false
                                        }
                                    } else{
                                        Toast.makeText(context,"wait for image to load",Toast.LENGTH_SHORT).show()
                                    }
                                    expanded = false
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
                        containerColor = Color.Black,
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

                                if (imageUrl.isNotEmpty()){
                                    shareOnlineImage(imageUrl,context,shareLauncher,scope){ isError,errorMessage ->
                                        if(isError){
                                            Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else{
                                    Toast.makeText(context,"wait for image to load",Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }


                    }, containerColor = Color.Black
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = imageModifier
            )
        }


        if (addResult) {

            when (addFavoriteImageResponse) {
                is Resource.Error -> {
                    isLoading = false
                    val err = (addFavoriteImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    addResult = false
                }

                is Resource.Success -> {
                    isLoading = false
                    val success = (addFavoriteImageResponse as Resource.Success<Message>).data
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
@OptIn(DelicateCoroutinesApi::class)
fun downloadImage(
    imageUrl: String,
    onSuccess:(Drawable) -> Unit,
    context: Context
){
    val imageLoader = ImageLoader.Builder(context)
        .build()

    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()

    GlobalScope.launch(Dispatchers.IO) {
        val result = (imageLoader.execute(request) as SuccessResult).drawable
        onSuccess(result)
    }
}





fun saveBitmapToDevice(
    context: Context,
    bitmap: Bitmap,
    fileName: String
) {
    val imageOutStream: OutputStream = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        resolver.openOutputStream(imageUri!!)!!
    } else {
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imageDir,fileName)
        FileOutputStream(imageFile)
    }
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,imageOutStream)
    imageOutStream.close()
}


@Composable
fun ShareImageUsingGlide(
    imageUrl: String,
    context: Context,
    scope: CoroutineScope
){



}

fun downloadImage(context: Context,imageUrl: String) {
    val request = DownloadManager.Request(Uri.parse(imageUrl))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        .setTitle("Image Downloaded")
        .setDescription("Downloading Image.....")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"image.jpg")

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)

//    val downloadId = downloadManager.enqueue(request)
//
//    val imageUri = downloadManager.getUriForDownloadedFile(downloadId)
}


fun shareOnlineImage(
    imageUrl: String,
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    scope: CoroutineScope,
    isError: (Boolean,String) -> Unit,
){
    val filename = "downloaded_image.jpg"
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


    val request = DownloadManager.Request(Uri.parse(imageUrl))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        .setTitle("Image Downloaded")
        .setDescription("Downloading Video.....")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)

    val downloadId = downloadManager.enqueue(request)


    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadId2 = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (downloadId2 == downloadId){
                val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + filename
                shareImage(context!!,filePath)
                context.unregisterReceiver(this)
            }
        }
    }

    context.registerReceiver(onComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))



}

fun downloadImageToExternalStorage(imageUrl: String,context: Context){
    val fileName = "downloaded_image.jpg"

    if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
        val externalStoragePublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(externalStoragePublicDir,fileName)

    }
}
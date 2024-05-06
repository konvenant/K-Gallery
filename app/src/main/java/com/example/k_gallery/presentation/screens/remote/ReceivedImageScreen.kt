package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.ImageObject
import com.example.k_gallery.data.dataSources.api.models.ImageX
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SentImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ReceivedImageScreen(
    navController: NavController,
    email: String,
    userViewModel: UserViewModel,
) {

    LaunchedEffect(Unit){
        userViewModel.getReceivedImages(email)
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val sentImageList by userViewModel.receivedImages.observeAsState()


    var isLoading by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    var selected by remember {
        mutableStateOf(false)
    }

    var cancel by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<ImageX>()
    }



    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }

    var favoriteResult by remember { mutableStateOf(false) }
    var favoriteConfirmation by remember { mutableStateOf(false) }

    val deleteMultipleSentImage by userViewModel.deleteMultipleSentImages.observeAsState()
    val addMultipleSentImageToFavorite by userViewModel.addMultipleFavoriteImages.observeAsState()
    var selectedImagesUrl : List<String>

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}



    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if(selected){
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "${selectedImages.size} Items Selected")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            selected = !selected
                            cancel = !cancel
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = {
                            deletionConfirmation = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }


                        IconButton(onClick = {

                            selectedImagesUrl = selectedImages.map { it.imageUrl }
                            if (selectedImagesUrl.isNotEmpty()){
                                downloadAndShareMultipleImages(selectedImagesUrl,context){show,error->
                                    if (show){
                                        Toast.makeText(context,error,Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            favoriteConfirmation = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null
                            )
                        }
                    }
                )
            } else{
                TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue,
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = "Received Images")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Blue)
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Sent Images", color = Color.White) },
                            onClick = {
                                navController.navigate(NavHelper.SentImageScreen.route + "/$email")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PictureInPicture,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = "Sent Videos", color = Color.White) },
                            onClick = {
                                navController.navigate(NavHelper.SentVideoScreen.route + "/$email")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VideoChat,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(text = "Received Videos", color = Color.White) },
                            onClick = {
                                navController.navigate(NavHelper.ReceivedVideoScreen.route + "/$email")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FeaturedVideo,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        )


                    }
                }
            )
        }
        },

    ) {


        when(sentImageList){
            is Resource.Loading -> {
                isLoading = true
                Box(modifier = Modifier.fillMaxSize()){
                    if(isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            is Resource.Success -> {
                isLoading = false
                val sentImages = (sentImageList as Resource.Success<SentImages>).data!!.images

                val filteredImage = sentImages.filter { image ->
                    !image.isRecieverDeleted || !image.isSenderDeleted
                }

                SentImageList(
                    images = filteredImage,
                    onClick = {
                        val imageIndex = filteredImage.indexOf(it).toString()
                        navController.navigate(NavHelper.ViewAllReceivedImageScreen.route + "/$imageIndex" + "/$email")
                    },
                    selected = selected,
                    selectedChange = { selected = !selected },
                    enabled = selected,
                    selectedImagesChange = { images ->
                        selectedImages.clear()
                        selectedImages.addAll(images)
                    },
                    cancelAction = cancel
                )
            }

            is Resource.Error -> {
                isLoading = false
                val errMessage = (sentImageList as Resource.Error<SentImages>).message
                Toast.makeText(context,errMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }


        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images? ",
                deletedAccepted = {
                    val arrayOfObjects = selectedImages.map {
                        ImageObject(
                            it.fromEmail,
                            it.toEmail,
                            it._id
                        )
                    }
                    val request = SentImageDeleteRequest(
                        email = email,
                        arrayOfObjects = arrayOfObjects
                    )
                    userViewModel.deleteMultipleSentImage(request)
                    deletionConfirmation = false
                    deletionResult = true
                    cancel = !cancel
                    selected = !selected
                }
            ) {
                deletionConfirmation = false
            }
        }

        if (deletionResult) {
            var isLoading2 by remember {
                mutableStateOf(false)
            }
            when (deleteMultipleSentImage) {
                is Resource.Error -> {
                    isLoading2 = false
                    val err = (deleteMultipleSentImage as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    deletionResult = false
                }

                is Resource.Success -> {
                    isLoading2 = false
                    val success = ( deleteMultipleSentImage as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    deletionResult = false
                    userViewModel.getReceivedImages(email)
                }

                is Resource.Loading -> {
                    isLoading2 = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading2) {
                            LoadingDialog()
                        }
                    }
                }

                else -> {

                }

            }
        }


        if (favoriteConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Add ${selectedImages.size} Images to Favorite",
                desc = "Are you sure you want to add these images to Favorite? ",
                deletedAccepted = {
                    val favoriteImages = selectedImages.map {
                        it.imageUrl
                    }
                    val request = FavoriteImageAddRequest(
                        email = email,
                        favoriteImages = favoriteImages
                    )
                    userViewModel.addMultipleFavoriteImage(request)
                    favoriteConfirmation = false
                    favoriteResult = true
                    cancel = !cancel
                    selected = !selected
                }
            ) {
                favoriteConfirmation = false
            }
        }

        if (favoriteResult) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (addMultipleSentImageToFavorite) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (addMultipleSentImageToFavorite as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( addMultipleSentImageToFavorite as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                }

                is Resource.Loading -> {
                    isLoading3 = true
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isLoading3) {
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



 fun downloadImages(imageUrls: List<String> , context:Context) {
   val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

     for ((index, imageUrl) in imageUrls.withIndex()) {
         val request = DownloadManager.Request(Uri.parse(imageUrl))
             .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
             .setTitle("Image Downloaded")
             .setDescription("Downloading image ${index + 1}....")
             .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
             .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image_${index+1}.jpg")

         downloadManager.enqueue(request)
     }
}

fun shareMultipleOnlineImages(context: Context, imageUrls: List<String>){
   downloadImages(imageUrls, context)


    val downloadCompleteReceiver = object  : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action){
                val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query =  DownloadManager.Query()
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)

                val cursor = downloadManager.query(query)
                val downloadedFileUris = mutableListOf<Uri>()

                if (cursor.moveToFirst()){
                    do {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val downloadedFileUri = Uri.parse(cursor.getString(columnIndex))
                        downloadedFileUris.add(downloadedFileUri)
                    } while (cursor.moveToNext())
                }
                cursor.close()

                val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                shareIntent.type = "image/*"
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(downloadedFileUris))
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(Intent.createChooser(shareIntent,"Share Images"))
            }
        }

    }

    context.registerReceiver(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}


fun downloadAndShareMultipleImages(
    imageUrls: List<String>,
    context: Context,
    showError : (Boolean,String) -> Unit
){

    val downloadManger = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadedFiles = mutableListOf<String>()

    for(imageUrl in imageUrls) {
        val fileName = "image_${imageUrls.indexOf(imageUrl)}.jpg"
        val request = DownloadManager.Request(Uri.parse(imageUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,fileName)
        request.setTitle("Image Download ")

        val downloadId = downloadManger.enqueue(request)

        object : AsyncTask<Unit, Unit, Boolean>() {
            @Deprecated("Deprecated in Java")
            @SuppressLint("Range")
            override fun doInBackground(vararg params: Unit?): Boolean {
                var downloaded = false

                while (!downloaded){
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManger.query(query)
                    if (cursor.moveToFirst()){
                        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL){
                            downloaded = true
                            downloadedFiles.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/"+fileName)
                        } else if(status == DownloadManager.STATUS_FAILED){
                            downloadManger.remove(downloadId)
                            return false
                        }
                    }
                    cursor.close()
                    Thread.sleep(500)
                }
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                if (result == true) {
                    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                    shareIntent.type = "image/*"
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(downloadedFiles.map { Uri.parse(it) }))
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(shareIntent,"Share Image"))
                } else{
                    showError(true,"cannot share images")
                }
            }

        }.execute()

    }

}

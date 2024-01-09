package com.example.k_gallery.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.VideosInFolderViewModel

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosInFolderScreen(
    navController: NavController,
    folderId: String,
    folderName: String
) {

    val videosInFolderViewModel: VideosInFolderViewModel = hiltViewModel()
    val folderSize = videosInFolderViewModel.folderMetrics.value?.first
    val folderCount = videosInFolderViewModel.folderMetrics.value?.second.toString()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    LaunchedEffect(true){
       videosInFolderViewModel.getVideosInFolder(folderId)
        Log.e("LAUNCHED" , " working correctly")
    }

    val videos by videosInFolderViewModel.img.observeAsState()
    val context = LocalContext.current

    var selected by remember {
        mutableStateOf(false)
    }

    var cancel by remember {
        mutableStateOf(false)
    }

    val selectedVideos = remember {
        mutableStateListOf<Video>()
    }
    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }
    val deleteImagLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ){ activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK ){
                Toast.makeText(context,"Deleted Video Successfully", Toast.LENGTH_LONG).show()
                deletionResult = true
            }
            else{
                val err = videosInFolderViewModel.multipleDeleteErr
                Toast.makeText(context,"Error Deleting Video : $err", Toast.LENGTH_LONG).show()
            }
        }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (selected){
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "${selectedVideos.size} Videos Selected")
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
                            shareMultipleVideos(context, selectedVideos.map { it.videoUri })
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = folderName)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$folderCount items    $folderSize",
                                style = TextStyle.Default.copy(
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ){

            when(videos){
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    val errorMessage = (videos as Resource.Error<List<Video>>).message
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val videoList = (videos as Resource.Success<List<Video>>).data
                        VideosList(
                            videos = videoList!!,
                            onClick = {
                                val imageIndex = videoList.indexOf(it).toString()
                                navController.navigate(NavHelper.VideoScreen.route + "/$imageIndex"+"/$folderId")
                            },
                            selected = selected,
                            selectedChange = {
//                            selected = it
                                selected = !selected
                            },
                            enabled = selected,
                            selectedImagesChange = { videos ->
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                            },
                            cancelAction = cancel
                        )
                }

                else -> {
                    Log.e("ELSE", "$videos")
                }
            }
        }

        if (deletionResult){
            LaunchedEffect(Unit){
                val message = if(deletionResult) "Videos deleted successfully" else "Failed to delete Videos"
                val duration = Toast.LENGTH_LONG
                Toast.makeText(context,message,duration).show()
                videosInFolderViewModel.getVideosInFolder(folderId)
                cancel = !cancel
                selected = !selected
            }
        }

        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedVideos.size} Videos",
                desc = "Are you sure want to delete these videos? ",
                deletedAccepted = {
                    videosInFolderViewModel.deleteMultipleVideos(
                        selectedVideos,
                        deleteImagLauncher,
                        context
                    )
                    deletionConfirmation = false
                }
            ) {
                deletionConfirmation = false
            }
        }
     }

      }


@Composable
fun VideosList(
    videos: List<Video>,
    onClick: (Video) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<Video>) -> Unit,
    cancelAction: Boolean
){

    val selectedVideos = remember {
        mutableStateListOf<Video>()
    }

    var isEnabled by remember {
        mutableStateOf(enabled)
    }

    var selectedAllChecked by remember {
        mutableStateOf(false)
    }

    var showDeleteTitle by remember {
        mutableStateOf(selected)
    }




    LaunchedEffect(cancelAction){
        selectedVideos.clear()
        selectedAllChecked = selectedVideos.size == videos.size
    }

    LazyColumn(modifier = Modifier.fillMaxSize()){
        item {
            if (enabled){
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages", "$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else {
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
                        }
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select all",
                        modifier = Modifier.clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages", "$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else {
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
                        }
                    )
                    Checkbox(
                        checked = selectedAllChecked,
                        onCheckedChange = {
                            selectedAllChecked = it
                            if (it){
                                selectedVideos.clear()
                                selectedVideos.addAll(videos)
                                Log.d("selectedImages","$selectedVideos")
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else{
                                selectedVideos.clear()
                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedVideos)
                        }
                    )
                }
            }
        }


        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(900.dp),
                contentPadding = PaddingValues(top = 40.dp)
            ){
                items(videos){ video ->
                    val isSelected = selectedVideos.contains(video)
                    VideoItem(
                        video = video,
                        isEnabled = enabled,
                        onLongClick = {
                            if (!isSelected){
                                selectedVideos.add(video)
                            }
                            isEnabled = !isEnabled
                            selectedChange(isEnabled)
                            selectedImagesChange(selectedVideos)
                        },
                        onCheckedChange = { isChecked ->
                            if (isChecked){
                                selectedVideos.add(video)
                            } else {
                                selectedVideos.remove(video)
                                if(selectedVideos.size == 0){
                                    selectedChange(isEnabled)
                                }
                            }
                            selectedAllChecked = selectedVideos.size == videos.size
                            selectedImagesChange(selectedVideos)

                            Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                        },
                        onClick = {
                            if(enabled){

                                if (!isSelected){
                                    selectedVideos.add(video)
                                } else {
                                    selectedVideos.remove(video)
                                    if(selectedVideos.size == 0){
                                        selectedChange(isEnabled)
                                    }
                                }
                                selectedAllChecked = selectedVideos.size == videos.size
                                selectedImagesChange(selectedVideos)

                                Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                            } else{
                                onClick(it)
                            }
                        },
                        selectedItem = selectedVideos.contains(video)
                    )
                }
            }


        }
    }
}


@Composable
fun AllVideosList(
    videos: List<Video>,
    onClick: (Video) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<Video>) -> Unit,
    cancelAction: Boolean
){
    val selectedVideos = remember {
        mutableStateListOf<Video>()
    }

    var isEnabled by remember {
        mutableStateOf(enabled)
    }

    var selectedAllChecked by remember {
        mutableStateOf(false)
    }

    var showDeleteTitle by remember {
        mutableStateOf(selected)
    }

    LaunchedEffect(cancelAction){
        selectedVideos.clear()
        selectedAllChecked = selectedVideos.size == videos.size
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = Modifier.fillMaxWidth().height(700.dp),
        contentPadding = PaddingValues(top = 40.dp)
    ){
        items(videos){ video ->
            val isSelected = selectedVideos.contains(video)
            VideoItem(
                video = video,
                isEnabled = enabled,
                onLongClick = {
                    if (!isSelected){
                        selectedVideos.add(video)
                    }
                    isEnabled = !isEnabled
                    selectedChange(isEnabled)
                    selectedImagesChange(selectedVideos)
                },
                onCheckedChange = { isChecked ->
                    if (isChecked){
                        selectedVideos.add(video)
                    } else {
                        selectedVideos.remove(video)
                        if(selectedVideos.size == 0){
                            selectedChange(isEnabled)
                        }
                    }
                    selectedAllChecked = selectedVideos.size == videos.size
                    selectedImagesChange(selectedVideos)

                    Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                },
                onClick = {
                    if(enabled){

                        if (!isSelected){
                            selectedVideos.add(video)
                        } else {
                            selectedVideos.remove(video)
                            if(selectedVideos.size == 0){
                                selectedChange(isEnabled)
                            }
                        }
                        selectedAllChecked = selectedVideos.size == videos.size
                        selectedImagesChange(selectedVideos)

                        Log.e("IMAGE-ITEMS", "${selectedVideos.size}")
                    } else{
                        onClick(it)
                    }
                },
                selectedItem = selectedVideos.contains(video)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoItem(
    video: Video,
    onClick: (Video) -> Unit,
    selectedItem: Boolean,
    onLongClick: () -> Unit,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
){

    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = video.uri,
        imageLoader = imageLoader
    )
    var selected by remember {
        mutableStateOf(selectedItem)
    }

    val padding = if(selected) 4.dp else 6.dp

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(video) }
        .padding(padding)
        .combinedClickable(
            onLongClick = {
                onLongClick()
            },
            onClick = { onClick(video) }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onClick(video) },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent)
                    .align(Alignment.Center)
                ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            if (isEnabled){
                Checkbox(
                    checked = selectedItem,
                    onCheckedChange = {
                        onCheckedChange(it)
                        selected = it
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

fun shareMultipleVideos(
    context: Context,
    videosUris: List<Uri>
){
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "video/*"
        putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList<Parcelable>(videosUris)
        )
    }
    context.startActivity(Intent.createChooser(shareIntent,"Share Images"))
}
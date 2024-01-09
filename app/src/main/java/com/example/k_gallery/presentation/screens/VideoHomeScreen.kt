package com.example.k_gallery.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.data.dataSources.local.VideoFolder
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import com.example.k_gallery.presentation.viewmodel.FolderViewModel
import com.example.k_gallery.presentation.viewmodel.VideoFolderViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

@Composable
fun VideoHomeScreen(
    navController: NavController
){

    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    if (permissionState.hasPermission) {
      VideoContentScreen(navController)
    } else {
        val textToshow = if (permissionState.shouldShowRationale) {
            "This permission is important for this app"
        } else {
            "Unavailable"
        }

        Text(text = textToshow)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            permissionState.launchPermissionRequest()
        }) {
            Text(text = "Request Permission")
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoContentScreen(
    navController: NavController
) {

    val videoFolderViewModel: VideoFolderViewModel = hiltViewModel()

    val videoFolder by videoFolderViewModel.videoFolder.observeAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    var showSnackbar by remember {
        mutableStateOf(false)
    }
    var errorMessageFromComposable by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = showSnackbar ){
        if(showSnackbar){
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = "Error: $errorMessageFromComposable",
                        actionLabel = "Try again",
                        duration = SnackbarDuration.Indefinite
                    )
                when(result){
                    SnackbarResult.ActionPerformed -> {

                    }
                    SnackbarResult.Dismissed -> {

                    }
                }
            }
        }
    }


    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.klogo),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "K-Gallery")
                    }
                },
                scrollBehavior = scrollBehavior
            )

        },
        bottomBar = { VideoBottomNavigation(navController = navController) }
            ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when(videoFolder){
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    val errorMessage = (videoFolder as Resource.Error<List<VideoFolder>>).message
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                    showSnackbar = true
                    errorMessageFromComposable = errorMessage!!
                }
                is Resource.Success -> {
                    val folders = (videoFolder as Resource.Success<List<VideoFolder>>).data
                    VideoFolderList(videoFolders = folders!!, navController = navController){
                        navController.navigate(NavHelper.VideosInFolderScreen.route+"/${it.id.toString()}/${it.name}")
                    }
                }

                else -> {
                    Text(
                        text = "Else Block",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }

    }
}




@Composable
fun VideoFolderList(
    videoFolders: List<VideoFolder>,
    navController: NavController,
    onItemClick: (VideoFolder) -> Unit
){

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            SelectionBoxes(navController, onPhotoClick = {
                navController.navigate(NavHelper.AllVideosScreen.route){
                    popUpTo(NavHelper.VideoHomeScreen.route){
                        inclusive = true
                    }
                }
            }, onFolderClick = {

            })
        }
        item{
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(900.dp),
                contentPadding = PaddingValues(top = 20.dp, start = 4.dp, end = 8.dp, bottom = 60.dp)
            ){
                items(videoFolders){ folder ->
                    VideoFolderItem(folder = folder, onItemClick = onItemClick )
                }
            }
        }
    }
}

@Composable
fun VideoFolderItem(
    folder: VideoFolder,
    onItemClick: (VideoFolder) -> Unit
){

    val context = LocalContext.current
    var visible by rememberSaveable { mutableStateOf(false) }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = folder.descVideoUri,
        imageLoader = imageLoader
    )

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(folder) }
            .padding(8.dp)
    ){
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        folder.name?.let {
            Text(
                text = it,
                style = TextStyle.Default.copy(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = folder.videoCount.toString()+" items"+"  ${folder.folderSize}  ",
                style = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )

        }

    }
}



@Composable
fun VideoBottomNavigation (navController: NavController) {
    NavigationBar (
        containerColor = Color.White
            ) {
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(NavHelper.HomeScreen.route){
                    popUpTo(NavHelper.VideoHomeScreen.route){
                        inclusive = true
                    }
                }
            },
            label = {
                Text(text = "Images")
            },
            icon = {
                Icon(imageVector = Icons.Default.Image, contentDescription = null)
            }
        )


        NavigationBarItem(
            selected = true,
            onClick = {

            },
            label = {
                Text(text = "Videos")
            },
            icon = {
                Icon(imageVector = Icons.Default.FeaturedVideo, contentDescription = null)
            }
        )
    }
}

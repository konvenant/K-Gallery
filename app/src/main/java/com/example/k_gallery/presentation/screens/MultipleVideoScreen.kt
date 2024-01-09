package com.example.k_gallery.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import com.example.k_gallery.presentation.viewmodel.AllVideoViewModel
import com.example.k_gallery.presentation.viewmodel.VideosInFolderViewModel
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultipleVideoScreen(
    navController: NavController,
    videoIndex: String
) {
    val allVideosViewModel : AllVideoViewModel = hiltViewModel()
    val context = LocalContext.current

    var isVisible by remember {
        mutableStateOf(true)
    }
    val scale = remember {
        mutableFloatStateOf(1f)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var imgName by remember {
        mutableStateOf("")
    }
    var imgPath by remember {
        mutableStateOf("")
    }
    var imgDate by remember {
        mutableStateOf("")
    }
    var imgSize by remember {
        mutableStateOf("")
    }
    var encodedPath by remember {
        mutableStateOf("")
    }

    var videoUri by remember {
        mutableStateOf("")
    }

    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom
    }


    val favStatus by allVideosViewModel.favStatus.observeAsState()
    var favouriteResult by remember { mutableStateOf(false) }

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
                val err = allVideosViewModel.deleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
        }

    val pagerState = rememberPagerState(videoIndex.toInt())

    val videoList by allVideosViewModel.allVideos.observeAsState()

        Log.d("TAG","$videoList")




    Scaffold(
        topBar = {
            if (isVisible) {
                TopAppBar(
                    title = {

                    },
                    actions = {

                        IconButton(onClick = {
                            allVideosViewModel.addVideoToFavorites(Uri.parse(videoUri), context)
                            favouriteResult = true
                        }) {
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
                                onClick = {  navController.navigate(NavHelper.DetailsScreen.route+"/${imgName}"+"/${encodedPath}"+"/${imgDate}"+"/${imgSize}") }
                            )

                            DropdownMenuItem(
                                text = { Text(text = "Open in Gallery") },
                                onClick = { openVideo(context,videoUri) }
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
                        IconButton(onClick = { shareVideo(context,imgPath) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {deletionConfirmation = true}) {
                            Icon(
                                imageVector = Icons.Default.Delete,
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
            when (videoList) {
                is Resource.Success -> {

                    val image = (videoList as Resource.Success<List<Video>>).data

                    image?.let {
                        HorizontalPager(
                            pageCount = it.size,
                            state = pagerState,
                            userScrollEnabled = isVisible
                        ) { index ->
                            imgName = it[index].name
                            imgPath = it[index].uri
                            videoUri = it[index].videoUri.toString()
                            encodedPath = Uri.encode(imgPath)
                            imgDate = it[index].dateTaken.toString()
                            imgSize = calculateImageSize(it[index].size)
                            VideoPlayer(Uri.parse(imgPath), isVisible = {
                                isVisible = !isVisible
                            })
                        }
                    }
                }
                else -> {}
            }
        }

        if (deletionResult){
            LaunchedEffect(Unit){
                val message = if(deletionResult) "Video deleted successfully" else "Failed to delete video"
                val duration = Toast.LENGTH_LONG
                Toast.makeText(context,message,duration).show()
                navController.navigateUp()
            }
        }

        if (favouriteResult) {
            LaunchedEffect(Unit){
                delay(1000)

                when(favStatus){
                    is Resource.Loading -> {
                        Toast.makeText(context,"Loading...",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Success -> {
                        val message = (favStatus as Resource.Success).message
                        Toast.makeText(context,"Video Successfully Added to Favorite ",Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        val message = (favStatus as Resource.Error).message
                        Toast.makeText(context,"Error $message",Toast.LENGTH_LONG).show()
                    }

                    else -> {

                    }
                }
                favouriteResult = false
            }
        }

        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete Video",
                desc = "Are you sure want to delete this Video?",
                deletedAccepted = {
                    allVideosViewModel.deleteVideo(
                        Uri.parse(videoUri),
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







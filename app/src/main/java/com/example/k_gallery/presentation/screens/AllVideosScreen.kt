package com.example.k_gallery.presentation.screens

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.AllVideoViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVideosScreen(
    navController: NavController
) {
    val allVideosViewModel : AllVideoViewModel = hiltViewModel()
    val folderSize = allVideosViewModel.folderMetrics.observeAsState().value?.first.toString()
    val folderCount = allVideosViewModel.folderMetrics.observeAsState().value?.second.toString()
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
                Toast.makeText(context,"Deleted Image Successfully", Toast.LENGTH_LONG).show()
                deletionResult = true
            }
            else{
                val err = allVideosViewModel.multipleDeleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
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


    Scaffold(
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
                        Column(Modifier.fillMaxWidth()) {
                            Row(
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
                    scrollBehavior = scrollBehavior
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = { VideoBottomNavigation(navController) }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it))
        {
            val allVideos by allVideosViewModel.allVideos.observeAsState()
            when(allVideos) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    val errorMessage = (allVideos as Resource.Error<List<Video>>).message
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
                    val video = (allVideos as Resource.Success<List<Video>>).data
                    video?.let { vid ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ){
                            item {
                                SelectionBoxes(navController, onPhotoClick = {}, onFolderClick = {
                                    navController.navigate(NavHelper.VideoHomeScreen.route){
                                        popUpTo(NavHelper.AllVideosScreen.route){
                                            inclusive = true
                                        }
                                    }
                                })
                            }
                            item{
                                AllVideosList(
                                    videos = vid,
                                    onClick = {
                                        val videoIndex = video.indexOf(it).toString()
                                        navController.navigate(NavHelper.MultipleVideoScreen.route + "/$videoIndex")
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
                        }
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
        if (deletionResult){
            LaunchedEffect(Unit){
                val message = if(deletionResult) "Videos deleted successfully" else "Failed to delete Videos"
                val duration = Toast.LENGTH_LONG
                Toast.makeText(context,message,duration).show()
              allVideosViewModel.getAllVideos()
                cancel = !cancel
                selected = !selected
            }
        }

        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedVideos.size} Videos",
                desc = "Are you sure want to delete these videos? ",
                deletedAccepted = {
                    allVideosViewModel.deleteMultipleVideos(
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

package com.example.k_gallery.presentation.screens

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.PhotosViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    navController: NavController
) {

    val photosViewModel : PhotosViewModel = hiltViewModel()

    val folderSize = photosViewModel.folderMetrics.observeAsState().value?.first.toString()
    val folderCount = photosViewModel.folderMetrics.observeAsState().value?.second.toString()
    val context = LocalContext.current
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

    var selected by remember {
        mutableStateOf(false)
    }

    var cancel by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<Image>()
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
                val err = photosViewModel.multipleDeleteErr
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
                            shareMultipleImages(context, selectedImages.map { it.uri })
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
        bottomBar = { PhotosBottomNavigation(navController = navController) }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it))
        {
            val photosImages by photosViewModel.photoImages.observeAsState()
            when(photosImages) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    val errorMessage = (photosImages as Resource.Error<List<Image>>).message
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
                    val imgs = (photosImages as Resource.Success<List<Image>>).data
                    imgs?.let { image ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ){
                            item {
                                SelectionBoxes(navController, onPhotoClick = {}, onFolderClick = {
                                    navController.navigate(NavHelper.HomeScreen.route){
                                        popUpTo(NavHelper.PhotosScreen.route){
                                            inclusive = true
                                        }
                                    }
                                })
                            }
                            item{
                                PhotoImageList(
                                    images = image,
                                    onClick = {
                                        val imageIndex = imgs.indexOf(it).toString()
                                        navController.navigate(NavHelper.PhotosImageScreen.route + "/$imageIndex")
                                    },
                                    selected = false,
                                    selectedChange = {
//                            selected = it
                                        selected = !selected
                                    },
                                    enabled = selected,
                                    selectedImagesChange = { images ->
                                        selectedImages.clear()
                                        selectedImages.addAll(images)
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
                val message = if(deletionResult) "Images deleted successfully" else "Failed to delete image"
                val duration = Toast.LENGTH_LONG
                Toast.makeText(context,message,duration).show()
                photosViewModel.getAllImages()
                cancel = !cancel
                selected = !selected
            }
        }

        if (deletionConfirmation){
            ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images? ",
                deletedAccepted = {
                    photosViewModel.deleteMultipleImage(
                        selectedImages,
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
fun PhotosBottomNavigation (navController: NavController) {
    NavigationBar (
        containerColor = Color.Transparent
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {

            },
            label = {
                Text(text = "Images")
            },
            icon = {
                Icon(imageVector = Icons.Default.Image, contentDescription = null)
            }
        )


        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(NavHelper.VideoHomeScreen.route){
                    popUpTo(NavHelper.HomeScreen.route){
                        inclusive = true
                    }
                }
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
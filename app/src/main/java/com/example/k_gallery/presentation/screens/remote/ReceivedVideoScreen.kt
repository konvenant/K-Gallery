package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoAddRequest
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SentVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentVideos
import com.example.k_gallery.data.dataSources.api.models.VideoObject
import com.example.k_gallery.data.dataSources.api.models.VideoX
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ReceivedVideoScreen(
    navController: NavController,
    email: String,
    userViewModel: UserViewModel,
) {

    LaunchedEffect(Unit){
        userViewModel.getReceivedVideos(email)
    }

    val sentVideoList by userViewModel.receivedVideos.observeAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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

    val selectedVideos = remember {
        mutableStateListOf<VideoX>()
    }

    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }

    var favoriteResult by remember { mutableStateOf(false) }
    var favoriteConfirmation by remember { mutableStateOf(false) }

    val deleteMultipleSentVideo by userViewModel.deleteMultipleSentVideos.observeAsState()
    val addMultipleSentVideosToFavorite by userViewModel.addMultipleFavoriteVideos.observeAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}


    var selectedVideoUrls : List<String>

    Scaffold (
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (selected){
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "${selectedVideos.size} Items Selected")
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
                            selectedVideoUrls = selectedVideos.map { it.videoUrl }
                            if (selectedVideoUrls.isNotEmpty()){
                                downloadAndShareMultipleVideos(selectedVideoUrls,context){show,error ->
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
                    Text(text = "Received Videos")
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
                            text = { Text(text = "Received Images", color = Color.White) },
                            onClick = {
                                navController.navigate(NavHelper.ReceivedImageScreen.route + "/$email")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PictureInPictureAlt,
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



        when(sentVideoList){
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
                val sentVideos = (sentVideoList as Resource.Success<SentVideos>).data!!.videos
                val filteredVideos = sentVideos.filter {video ->
                    !video.isRecieverDeleted || !video.isSenderDeleted
                }



                SentVideosList(
                    videos = filteredVideos,
                    onClick = {
                        val videoIndex = filteredVideos.indexOf(it).toString()
                        navController.navigate(NavHelper.ViewAllReceivedVideoScreen.route + "/$videoIndex" + "/$email")
                    },
                    selected = selected,
                    selectedChange = {
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

            is Resource.Error -> {
                isLoading = false
                val errMessage = (sentVideoList as Resource.Error<SentVideos>).message
                Toast.makeText(context,errMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }

        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedVideos.size} Videos",
                desc = "Are you sure want to delete these Videos? ",
                deletedAccepted = {
                    val arrayOfObjects = selectedVideos.map {
                        VideoObject(
                            it.fromEmail,
                            it.toEmail,
                            it._id
                        )
                    }
                    val request = SentVideoDeleteRequest(
                        email = email,
                        arrayOfObjects = arrayOfObjects
                    )
                    userViewModel.deleteMultipleSentVideo(request)
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
            when (deleteMultipleSentVideo) {
                is Resource.Error -> {
                    isLoading2 = false
                    val err = (deleteMultipleSentVideo as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    deletionResult = false
                }

                is Resource.Success -> {
                    isLoading2 = false
                    val success = ( deleteMultipleSentVideo as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    deletionResult = false
                    userViewModel.getReceivedVideos(email)
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
                title = "Add ${selectedVideos.size} Videos to Favorite",
                desc = "Are you sure you want to add these Videos to Favorite? ",
                deletedAccepted = {
                    val favoriteVideos = selectedVideos.map {
                        it.videoUrl
                    }
                    val request = FavoriteVideoAddRequest(
                        email = email,
                        favoriteVideos = favoriteVideos
                    )
                    userViewModel.addMultipleFavoriteVideo(request)
                    favoriteConfirmation = false
                    favoriteResult = true
                }
            ) {
                favoriteConfirmation = false
            }
        }

        if (favoriteResult) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (addMultipleSentVideosToFavorite) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (addMultipleSentVideosToFavorite as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( addMultipleSentVideosToFavorite as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                    cancel = !cancel
                    selected = !selected
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


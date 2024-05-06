package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavController
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.SentVideos
import com.example.k_gallery.presentation.screens.local.OnlineVideoPlayer
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.DelicateCoroutinesApi




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    DelicateCoroutinesApi::class
)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ViewAllSentVideosScreen(
    navController: NavController,
    imageIndex: String,
    email : String,
    userViewModel: UserViewModel
){

    val windowInsetsController = LocalView.current.windowInsetsController
    val context = LocalContext.current


    var isVisible by remember {
        mutableStateOf(true)
    }
    val scale = remember {
        mutableFloatStateOf(1f)
    }




    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }



    val pagerState = rememberPagerState(imageIndex.toInt())

    val videoList by userViewModel.sentVideos.observeAsState()
    LaunchedEffect(key1 = true){
        userViewModel.getSentVideos(email)
    }
    LaunchedEffect(key1 = isVisible){

//        if (!isVisible) {
//            windowInsetsController?.hide(WindowInsets.Type.systemBars())
//            windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        } else{
//            windowInsetsController?.show(WindowInsets.Type.systemBars())
//        }
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

    val addFavoriteImageResponse by userViewModel.addFavoriteVideo.observeAsState()

    var toEmail by remember {
        mutableStateOf("")
    }

    var fromEmail by remember {
        mutableStateOf("")
    }


    var videoId by remember {
        mutableStateOf("")
    }




    var addResult by remember { mutableStateOf(false) }

    var isLoading by remember {
        mutableStateOf(false)
    }


    val deleteSentVideoResponse by userViewModel.deleteSentVideo.observeAsState()


    var isIconBtnEnabled by remember {
        mutableStateOf(true)
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
                        IconButton(
                            enabled = isIconBtnEnabled,
                            onClick = {
                                addResult = true
                                userViewModel.addFavoriteVideo(email, videoUri)
                                isIconBtnEnabled = false
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
                                onClick = {  navController.navigate(NavHelper.DetailsScreen2.route+"/${imgName}"+"/${encodedPath}"+"/${imgDate}"+"/${imgSize}") }
                            )

                            var downloadStatus by remember {
                                mutableStateOf("")
                            }

                            DropdownMenuItem(

                                text = { Text(text = "Download") },

                                onClick = {
                                    if (videoUri.isNotEmpty()){
                                        downloadVideo(videoUri, context)
                                    } else{
                                        Toast.makeText(context,"wait for video to load",Toast.LENGTH_SHORT).show()
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
                        containerColor = Color.Transparent,
                    )
                )
            }
        },
        bottomBar = {
            if (isVisible){
                BottomAppBar (
                    actions = {
                        IconButton(onClick = {
                            shareOnlineVideo(videoUri,context, shareLauncher){ show, msg ->
                                if (show){
                                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { deletionConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
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
            when (videoList) {
                is Resource.Success -> {
                    val image = (videoList as Resource.Success<SentVideos>).data?.videos!!

                    val filteredVideos = image.filter { video ->
                        !video.isSenderDeleted
                    }

                    filteredVideos.let {
                        HorizontalPager(
                            pageCount = it.size,
                            state = pagerState,
                            userScrollEnabled = isVisible
                        ) { index ->
                            imgName = it[index].fromEmail
                            imgPath = it[index].toEmail
                            videoUri = it[index].videoUrl
                            videoId = it[index]._id
                            fromEmail = it[index].fromEmail
                            toEmail = it[index].toEmail
                            encodedPath = Uri.encode(imgPath)
                            imgDate = it[index].date.substring(0,22)
                            imgSize = "0MB"
                            OnlineVideoPlayer(videoUri, isVisible = {
                                isVisible = !isVisible
                            })
                        }
                    }
                }
                else -> {}
            }
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



        if (deletionResult){

            when (deleteSentVideoResponse) {
                is Resource.Error -> {
                    isIconBtnEnabled = true
                    isLoading = false
                    val err = (deleteSentVideoResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    addResult = false
                    deletionResult = false
                }

                is Resource.Success -> {
                    isIconBtnEnabled = false
                    isLoading = false
                    val success = (deleteSentVideoResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    addResult = false
                    deletionResult = false
                    navController.navigateUp()
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




        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete Video",
                desc = "Are you sure want to delete this Video?",
                deletedAccepted = {
                    deletionConfirmation = false
                    userViewModel.deleteSentVideo(fromEmail,toEmail,videoId)
                    deletionResult = true
                }
            ) {
                deletionConfirmation = false
            }
        }

    }
}



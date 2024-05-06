package com.example.k_gallery.presentation.screens.local

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SaveAlt
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.presentation.screens.remote.LoadingDialog
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserPreferences
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.util.calculateImageSize
import com.example.k_gallery.presentation.util.deleteVideo
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.presentation.viewmodel.VideosInFolderViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoScreen(
    navController: NavController,
    imageIndex: String,
    folderId: String,
    lifecycleOwner: LifecycleOwner
){

    val windowInsetsController = LocalView.current.windowInsetsController

    val videoViewModel : VideosInFolderViewModel = hiltViewModel()

    var isVisible by remember {
        mutableStateOf(true)
    }
    val scale = remember {
        mutableFloatStateOf(1f)
    }

    val context = LocalContext.current

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }

    val userPref by remember{
        mutableStateOf(userPrefManager.getLoggedInPrefs())
    }

    var stateLoading by remember {
        mutableStateOf(false)
    }

    val userViewModel: UserViewModel = hiltViewModel()



    val favStatus by videoViewModel.favStatus.observeAsState()
    var favouriteResult by remember { mutableStateOf(false) }
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
                val err = videoViewModel.deleteErr
                Toast.makeText(context,"Error Deleting Image : $err", Toast.LENGTH_LONG).show()
            }
        }

    val zoomState = rememberTransformableState{zoom, _, _ ->
        scale.floatValue *=  zoom

    }
    val pagerState = rememberPagerState(imageIndex.toInt())

    val videoList by videoViewModel.img.observeAsState()
    LaunchedEffect(key1 = true){
        videoViewModel.getVideosInFolder(folderId)
        Log.d("TAG","$videoList")
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

    Scaffold(
        topBar = {
            if (isVisible) {
                TopAppBar(
                    title = {

                    },
                    actions = {
                        IconButton(onClick = {
                            videoViewModel.addVideoToFavorites(Uri.parse(videoUri),context)
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
                                onClick = { openVideo(context,imgPath) }
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
                        IconButton(onClick = { shareVideo(context,videoUri) }) {
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

                        IconButton(
                            onClick = {
                                performSaveSingleVideoToAccount(
                                    userViewModel,
                                    userPref,
                                    context,
                                    Uri.parse(videoUri),
                                    imgName,
                                    lifecycleOwner,
                                ){ state ->
                                    stateLoading = state
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
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

                    if (stateLoading){
                        LoadingDialog()
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
                    is Resource.Success<*> -> {
                        val message = (favStatus as Resource.Success).message
                        Toast.makeText(context,"Video Successfully Added to Favorite $message",Toast.LENGTH_LONG).show()
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
                    videoViewModel.deleteVideo(
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

@Composable
fun VideoPlayer(videoUri: Uri, isVisible: () -> Unit) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    var isPlaying by rememberSaveable {
        mutableStateOf(false)
    }

    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    val mContext = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(mContext).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = isPlaying
        }
    }

    val playPauseIcon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow


    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isPlaying = !isPlaying
                isVisible()
            }
    )




    DisposableEffect(Unit){

        val lifecycleObserver = LifecycleEventObserver { _,event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                exoPlayer.release()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

@Composable
fun OnlineVideoPlayer(videoUri: String, isVisible: () -> Unit) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    var isPlaying by rememberSaveable {
        mutableStateOf(false)
    }
    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    val mContext = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(mContext).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = isPlaying
        }
    }

    val playPauseIcon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow


    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isPlaying = !isPlaying
                isVisible()
            }
    )




    DisposableEffect(Unit){

        val lifecycleObserver = LifecycleEventObserver { _,event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                exoPlayer.release()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}










  fun shareVideo(context: Context, videoUri: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(videoUri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val chooser = Intent.createChooser(intent, "Share Video")
    context.startActivity(chooser)
}
  fun openVideo(context: Context, videoUri: String){
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(videoUri),"video/*")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
}

fun onDeleteVideoClicked(
    videoUri: Uri,
    context: Context,
    onDeleteNotification: (Boolean) -> Unit
) {
    val deletionResult = deleteVideo(videoUri,context)
    onDeleteNotification(deletionResult)
}




fun performSaveSingleVideoToAccount(
    userViewModel: UserViewModel,
    pref: UserPreferences,
    context: Context,
    videoUri : Uri,
    caption: String,
    lifecycleOwner: LifecycleOwner,
    showLoadingDialog: (Boolean) -> Unit
) {
    if (pref.isLoggedIn){
        userViewModel.saveSingleVideo(pref.email,videoUri,context,caption)
        userViewModel.saveVideoResponse.observe(
            lifecycleOwner, Observer { message ->
                when(message) {
                    is  Resource.Success ->{
                        val successMessage = "Video Saved Successfully"
                        showLoadingDialog(false)
                        Toast.makeText(context,successMessage,Toast.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> {
                        showLoadingDialog(true)
                    }

                    is Resource.Error ->{
                        val errMessage = message.message
                        showLoadingDialog(false)
                        Toast.makeText(context,errMessage,Toast.LENGTH_LONG).show()
                    }

                    else -> {

                    }
                }
            }
        )
    } else{
        Toast.makeText(context,"No Account Associated with this device, navigate to account to either SignUp or Login",Toast.LENGTH_LONG).show()
    }
}


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
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.sharp.ForwardToInbox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
import com.example.k_gallery.data.dataSources.api.models.ImageXX
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import com.example.k_gallery.data.dataSources.api.models.VideoXX
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OnlineFavoriteScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    anotherNavController: NavController
) {
    val composable1 = "composable1"
    val composable2 = "composable2"
    var currentComposable by remember {
        mutableStateOf(composable1)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        userViewModel.getFavoriteImages(email)
        userViewModel.getFavoriteVideos(email)
    }

    val savedImagesList by userViewModel.favoriteImages.observeAsState()
    val savedVideosList by userViewModel.favoriteVideos.observeAsState()

    var selected1 by remember {
        mutableStateOf(false)
    }
    var cancel1 by remember {
        mutableStateOf(false)
    }

    var selected2 by remember {
        mutableStateOf(false)
    }
    var cancel2 by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<ImageXX>()
    }

    val selectedVideos = remember {
        mutableStateListOf<VideoXX>()
    }
    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }


    var deletionResult2 by remember { mutableStateOf(false) }
    var deletionConfirmation2 by remember { mutableStateOf(false) }



    val deleteMultipleSavedVideo by userViewModel.deleteMultipleFavoriteVideos.observeAsState()

    val deleteMultipleSavedImage by userViewModel.deleteMultipleFavoriteImages.observeAsState()

    var selectedImagesUrl : List<String>

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}


    var selectedVideoUrls : List<String>



    var isSearchUser by remember { mutableStateOf(false) }

    val chatUsers by userViewModel.chatItemList.observeAsState()

    var emailToSearch by remember {
        mutableStateOf("")
    }

    val sheetState3 = rememberModalBottomSheetState()


    val userSearched by userViewModel.userSearchedResponse.observeAsState()

    var forwardImageHandler by remember { mutableStateOf(false) }

    var forwardVideoHandler by remember { mutableStateOf(false) }

    var forwardCaption by remember {
        mutableStateOf("")
    }

    var caption by remember {
        mutableStateOf("")
    }

    val color = if(caption.isNotEmpty()) Blue else Color.Red

    var isLoading2 by remember {
        mutableStateOf(false)
    }


    val forwardImageResponse by userViewModel.sendMultipleImageResponse.observeAsState()
    val forwardVideoResponse by userViewModel.sendMultipleVideoResponse.observeAsState()



    Scaffold (
        topBar = {
            if(selected1){
                TopAppBar(
                    title = {
                        Text(text = "${selectedImages.size} Items Selected")
                    },
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
                                downloadAndShareMultipleImages(selectedImagesUrl,context){ show, error ->
                                  if (show){
                                      Toast.makeText(context, error,Toast.LENGTH_LONG).show()
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
                            userViewModel.getChatItemList(email)
                            isSearchUser = true
                        }) {
                            Icon(
                                imageVector = Icons.Sharp.ForwardToInbox,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            selected1 = !selected1
                            cancel1 = !cancel1
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                        }
                    }
                )

            } else if(selected2){

                TopAppBar(
                    title = {
                        Text(text = "${selectedVideos.size} Items Selected")
                    },
                    actions = {
                        IconButton(onClick = {
                            deletionConfirmation2 = true
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
                            userViewModel.getChatItemList(email)
                            isSearchUser = true
                        }) {
                            Icon(
                                imageVector = Icons.Sharp.ForwardToInbox,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            selected2 = !selected2
                            cancel2 = !cancel2
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
            ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp,16.dp,0.dp,0.dp)
            ){
                item {
                    FavoriteChoiceSelectionBoxes(
                        onFirstClick = { currentComposable = composable1 },
                        onSecondClick = { currentComposable = composable2 },
                        firstText = "Favorite Images",
                        secondText = "Favorite Videos",
                        firstSelectedIcon = Icons.Filled.Image,
                        firstUnselectedIcon = Icons.Outlined.Image,
                        secondSelectedIcon = Icons.Filled.VideoLibrary,
                        secondUnselectedIcon = Icons.Outlined.VideoLibrary
                    )
                }
                item {
                    Box(Modifier.fillMaxSize()) {
                        if (currentComposable == composable1){


                            when(savedImagesList){
                                is Resource.Loading -> {
                                    isLoading = true
                                }
                                is Resource.Error -> {
                                    isLoading = false
                                    val errorMessage = (savedImagesList as Resource.Error<FavoriteImages>).message
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
                                    isLoading = false
                                    val savedImages = (savedImagesList as Resource.Success<FavoriteImages>).data?.images


                                    FavoriteImageList(
                                        images = savedImages!!,
                                        onClick = {
                                            val imageIndex = savedImages.indexOf(it).toString()
                                            anotherNavController.navigate(NavHelper.ViewAllFavoriteImageScreen.route + "/$imageIndex"+"/$email")
                                        },
                                        selected = selected1,
                                        selectedChange = {selected1 = !selected1} ,
                                        enabled = selected1,
                                        selectedImagesChange = {images ->
                                            selectedImages.clear()
                                            selectedImages.addAll(images)},
                                        cancelAction = cancel1
                                    )


                                }

                                else -> {}
                            }



                        } else if(currentComposable == composable2){







                            when(savedVideosList){
                                is Resource.Loading -> {
                                    isLoading = true
                                }
                                is Resource.Error -> {
                                    isLoading = false
                                    val errorMessage = (savedVideosList as Resource.Error<FavoriteVideos>).message
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
                                    isLoading = false
                                    val savedVideos = (savedVideosList as Resource.Success<FavoriteVideos>).data!!.videos

                                    FavoriteVideosList(
                                        videos = savedVideos,
                                        onClick = {
                                            val videoIndex = savedVideos.indexOf(it).toString()
                                            anotherNavController.navigate(NavHelper.ViewAllFavoriteVideoScreen.route + "/$videoIndex"+"/$email")
                                        },
                                        selected = selected2,
                                        selectedChange = {
                                            selected2 = !selected2
                                        },
                                        enabled = selected2,
                                        selectedImagesChange = {videos ->
                                            selectedVideos.clear()
                                            selectedVideos.addAll(videos)},
                                        cancelAction = cancel2
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }


        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images from Favorite? ",
                deletedAccepted = {
                    val ids = selectedImages.map {
                        it._id
                    }
                    val request = FavoriteImageDeleteRequest(
                        email = email,
                        arrayOfId = ids
                    )
                    userViewModel.deleteMultipleFavoriteImage(request)
                    deletionConfirmation = false
                    deletionResult = true
                    selected1 = !selected1
                    cancel1 = !cancel1
                }
            ) {
                deletionConfirmation = false
            }
        }

        if (deletionResult) {

            var isLoading2 by remember {
                mutableStateOf(false)
            }
            when (deleteMultipleSavedImage) {
                is Resource.Error -> {
                    isLoading2 = false
                    val err = (deleteMultipleSavedImage as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    deletionResult = false
                }

                is Resource.Success -> {
                    isLoading2 = false
                    val success = ( deleteMultipleSavedImage as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    deletionResult = false
                    userViewModel.getFavoriteImages(email)
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

        if (deletionConfirmation2){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedVideos.size} Videos",
                desc = "Are you sure want to delete these Videos from Favorite? ",
                deletedAccepted = {
                    val ids = selectedVideos.map {
                        it._id
                    }
                    val request = FavoriteVideoDeleteRequest(
                        email = email,
                        arrayOfId = ids
                    )
                    userViewModel.deleteMultipleFavoriteVideo(request)
                    deletionConfirmation2 = false
                    deletionResult2 = true
                    selected2 = !selected2
                    cancel2 = !cancel2
                }
            ) {
                deletionConfirmation2 = false
            }
        }

        if (deletionResult2) {

            var isLoading2 by remember {
                mutableStateOf(false)
            }
            when (deleteMultipleSavedVideo) {
                is Resource.Error -> {
                    isLoading2 = false
                    val err = (deleteMultipleSavedVideo as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    deletionResult2 = false
                }

                is Resource.Success -> {
                    isLoading2 = false
                    val success = ( deleteMultipleSavedVideo as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    deletionResult2 = false
                    userViewModel.getFavoriteVideos(email)
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




        if(isSearchUser){
            ModalBottomSheet(
                onDismissRequest = {
                    isSearchUser = false
                    emailToSearch = ""
                    userViewModel.userSearchedResponse.postValue(null)
                },
                sheetState = sheetState3
            ) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp
                    )) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Send Media", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = emailToSearch,
                                    onValueChange ={ emailToSearch = it } ,
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .padding(16.dp),
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                                    },
                                    placeholder = {
                                        Text(text = "Search Email")
                                    },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = color,
                                        unfocusedBorderColor = color
                                    ),
                                    singleLine = true
                                )


                                IconButton(
                                    onClick = {
                                        if (emailToSearch.isNotEmpty()){
                                            userViewModel.userSearchedResponse.postValue(null)
                                            userViewModel.getSearchedUser(emailToSearch)
                                        } else {
                                            Toast.makeText(context,"User Email is empty", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = color
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            when(userSearched){
                                is Resource.Success -> {
                                    val userGotten =  (userSearched as Resource.Success<User>).data?.user
                                    var once by remember{
                                        mutableStateOf(true)
                                    }

                                    if(once){
                                        once = false
                                    }

                                    if (userGotten!!.email == email){
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .clickable {
                                                    caption = "."
                                                    forwardCaption = "."
                                                    isSearchUser = false

                                                    if (selectedImages.isNotEmpty()) {
                                                        val request = ImageSendRequest(
                                                            selectedImages.map { it.imageUrl },
                                                            email,
                                                            userGotten.email,
                                                            caption
                                                        )
                                                        userViewModel.sendMultipleImage(request)
                                                    }

                                                    if (selectedVideos.isNotEmpty()) {
                                                        val request = VideoSendRequest(
                                                            selectedVideos.map{it.videoUrl},
                                                            email,
                                                            userGotten.email,
                                                            caption
                                                        )
                                                        userViewModel.sendMultipleVideo(request)
                                                    }
                                                    if (selectedImages.isNotEmpty()) {
                                                        forwardImageHandler = true
                                                    } else if (selectedVideos.isNotEmpty()) {
                                                        forwardVideoHandler = true
                                                    }
                                                }
                                        ) {
                                            AsyncImage(
                                                model = userGotten.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clickable {
                                                        isSearchUser = false

                                                        if (selectedImages.isNotEmpty()) {
                                                            val request = ImageSendRequest(
                                                                selectedImages.map{it.imageUrl},
                                                                email,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleImage(request)
                                                        }

                                                        if (selectedVideos.isNotEmpty()) {
                                                            val request = VideoSendRequest(
                                                                selectedVideos.map{it.videoUrl},
                                                                email,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleVideo(request)
                                                        }
                                                        if (selectedImages.isNotEmpty()) {
                                                            forwardImageHandler = true
                                                        } else if (selectedVideos.isNotEmpty()) {
                                                            forwardVideoHandler = true
                                                        }
                                                    }
                                                    .clip(CircleShape),
                                                placeholder = painterResource(id = R.drawable.full_logo),
                                                error = painterResource(id = R.drawable.logo)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = userGotten.email+"(me)", color = Red)
                                        }
                                    } else{
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .clickable {
                                                    isSearchUser = false
                                                    forwardCaption = "."
                                                    caption = "."
                                                    if (selectedImages.isNotEmpty()) {
                                                        val request = ImageSendRequest(
                                                            selectedImages.map{it.imageUrl},
                                                            email,
                                                            userGotten.email,
                                                            forwardCaption
                                                        )
                                                        userViewModel.sendMultipleImage(request)
                                                    }

                                                    if (selectedVideos.isNotEmpty()) {
                                                        val request = VideoSendRequest(
                                                            selectedVideos.map{it.videoUrl},
                                                            email,
                                                            userGotten.email,
                                                            forwardCaption
                                                        )
                                                        userViewModel.sendMultipleVideo(request)
                                                    }
                                                    if (selectedImages.isNotEmpty()) {
                                                        forwardImageHandler = true
                                                    } else if (selectedVideos.isNotEmpty()) {
                                                        forwardVideoHandler = true
                                                    }
                                                }
                                        ) {
                                            AsyncImage(
                                                model = userGotten.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clickable {
                                                        isSearchUser = false

                                                        if (selectedImages.isNotEmpty()) {
                                                            val request = ImageSendRequest(
                                                                selectedImages.map { it.imageUrl },
                                                                email,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleImage(request)
                                                        }

                                                        if (selectedVideos.isNotEmpty()) {
                                                            val request = VideoSendRequest(
                                                                selectedVideos.map{it.videoUrl},
                                                                email,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleVideo(request)
                                                        }
                                                        if (selectedImages.isNotEmpty()) {
                                                            forwardImageHandler = true
                                                        } else if (selectedVideos.isNotEmpty()) {
                                                            forwardVideoHandler = true
                                                        }
                                                    }
                                                    .clip(CircleShape),
                                                placeholder = painterResource(id = R.drawable.full_logo),
                                                error = painterResource(id = R.drawable.logo)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = if(userGotten.email.length > 23) userGotten.email.slice(0..23)+ ".." else userGotten.email, color = Blue)
                                        }
                                    }
                                }

                                is Resource.Loading -> {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .align(Alignment.Center)
                                        )
                                    }
                                }

                                is Resource.Error -> {
                                    val errMessage = (userSearched as Resource.Error<User>).message
                                    Toast.makeText(context,errMessage,Toast.LENGTH_LONG).show()
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Milk)) {
                                        Text(
                                            text = errMessage!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }

                                }

                                else -> {}
                            }

                        }

                    }

                    when(chatUsers){
                        is Resource.Success -> {
                            isLoading = false
                            val listOfChats = (chatUsers as Resource.Success<ChatItems>).data!!.chatItems

                            items(listOfChats){ chat ->
                                ChatSenderItem(
                                    chat = chat,
                                    clickAction = {
                                        isSearchUser = false
                                        forwardCaption = "."
                                        caption = "."
                                        if (selectedImages.isNotEmpty()) {
                                            val request = ImageSendRequest(
                                                selectedImages.map{it.imageUrl},
                                                email,
                                                chat.email,
                                                forwardCaption
                                            )
                                            userViewModel.sendMultipleImage(request)
                                        }

                                        if (selectedVideos.isNotEmpty()) {
                                            val request = VideoSendRequest(
                                                selectedVideos.map{it.videoUrl},
                                                email,
                                                chat.email,
                                                forwardCaption
                                            )
                                            userViewModel.sendMultipleVideo(request)
                                        }
                                        if (selectedImages.isNotEmpty()) {
                                            forwardImageHandler = true
                                        } else if (selectedVideos.isNotEmpty()) {
                                            forwardVideoHandler = true
                                        }
                                    },
                                    userViewModel = userViewModel,
                                    showProfile = false
                                )
                            }


                        }

                        is Resource.Loading -> {
                            item {
                                isLoading = true

                            }
                        }

                        is Resource.Error -> {
                            isLoading = false
                            val errorMessage = (chatUsers as Resource.Error<ChatItems>).message
                            Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show()
                        }

                        else -> {

                        }
                    }
                }
            }
        }


        if (forwardImageHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (forwardImageResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (forwardImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    forwardImageHandler = false
                    cancel1 = !cancel1
                    isSearchUser = false
                    selectedImages.clear()
                    selectedVideos.clear()
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( forwardImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,"Message Forwarded Successfully", Toast.LENGTH_LONG).show()
                    forwardImageHandler = false
                    caption = ""
                    selected1 = false
                    selected2 = false
                    cancel2 = false
                    cancel1 = false
                    isSearchUser = false
                    selectedImages.clear()
                    selectedVideos.clear()
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

        if (forwardVideoHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (forwardVideoResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (forwardVideoResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    forwardVideoHandler = false
                    selected1 = false
                    selected2 = false
                    cancel2 = false
                    cancel1 = false
                    isSearchUser = false
                    selectedImages.clear()
                    selectedVideos.clear()
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( forwardVideoResponse as Resource.Success<Message>).data
                    Toast.makeText(context,"Message Forwarded Successfully", Toast.LENGTH_LONG).show()
                    forwardVideoHandler = false
                    caption = ""
                    selected1 = false
                    selected2 = false
                    cancel2 = false
                    cancel1 = false
                    isSearchUser = false
                    selectedImages.clear()
                    selectedVideos.clear()
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


        Box(modifier = Modifier.fillMaxSize()){
            if(isLoading2) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

}


@Composable
fun FavoriteChoiceSelectionBoxes(
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    firstText:String,
    secondText: String,
    firstSelectedIcon: ImageVector,
    firstUnselectedIcon: ImageVector,
    secondSelectedIcon: ImageVector,
    secondUnselectedIcon: ImageVector
) {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    val selectionItems = listOf(
        TopNavigationItem(
            title = firstText,
            selectedIcon = firstSelectedIcon,
            unSelectedIcon = firstUnselectedIcon,
            action = { onFirstClick() }
        ),
        TopNavigationItem(
            title = secondText,
            selectedIcon = secondSelectedIcon,
            unSelectedIcon = secondUnselectedIcon,
            action = { onSecondClick() }
        )
    )


    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        selectionItems.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = {
                    selectedItem = index
                    item.action()

                },
                border = if(selectedItem == index){
                    BorderStroke(2.dp, Red)
                } else BorderStroke(1.dp, Blue),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(
                    imageVector = if (selectedItem == index) item.selectedIcon else item.unSelectedIcon,
                    contentDescription = null
                )
                Text(text = item.title)
            }
        }

    }
}




@SuppressLint("MutableCollectionMutableState")
@Composable
fun FavoriteImageList(
    images: List<ImageXX>,
    onClick: (ImageXX) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<ImageXX>) -> Unit,
    cancelAction: Boolean
){
    val selectedImages = remember {
        mutableStateListOf<ImageXX>()
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
        selectedImages.clear()
        selectedAllChecked = selectedImages.size == images.size
    }

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .height(900.dp)){
        item {
            if (enabled){
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAllChecked = !selectedAllChecked
                            if (selectedAllChecked) {
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages", "$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else {
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
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
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages", "$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else {
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
                        }
                    )
                    Checkbox(
                        checked = selectedAllChecked,
                        onCheckedChange = {
                            selectedAllChecked = it
                            if (it){
                                selectedImages.clear()
                                selectedImages.addAll(images)
                                Log.d("selectedImages","$selectedImages")
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else{
                                selectedImages.clear()
                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                                selectedChange(isEnabled)
                            }
                            selectedImagesChange(selectedImages)
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

                items(
                    images,
                    key = {
                         it._id
                    }
                ){ image ->
                    val isSelected = selectedImages.contains(image)

                    FavoriteImageItem(
                        image = image,
                        isEnabled = enabled,
                        onLongClick = {
                            if (!isSelected){
                                selectedImages.add(image)
                            }
                            isEnabled = !isEnabled
                            selectedChange(isEnabled)
                            selectedImagesChange(selectedImages)
                        },
                        onCheckedChange = { isChecked ->
                            if (isChecked){
                                selectedImages.add(image)
                            } else {
                                selectedImages.remove(image)
                                if(selectedImages.size == 0){
                                    selectedChange(isEnabled)
                                }
                            }
                            selectedAllChecked = selectedImages.size == images.size
                            selectedImagesChange(selectedImages)

                            Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                        },
                        onClick = {
                            if(enabled){

                                if (!isSelected){
                                    selectedImages.add(image)
                                } else {
                                    selectedImages.remove(image)
                                    if(selectedImages.size == 0){
                                        selectedChange(isEnabled)
                                    }
                                }
                                selectedAllChecked = selectedImages.size == images.size
                                selectedImagesChange(selectedImages)

                                Log.e("IMAGE-ITEMS", "${selectedImages.size}")
                            } else{
                                onClick(it)
                            }
                        },
                        selectedItem = selectedImages.contains(image)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteImageItem(
    image: ImageXX,
    onClick: (ImageXX) -> Unit,
    selectedItem: Boolean,
    onLongClick: () -> Unit,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
){

    var selected by remember {
        mutableStateOf(selectedItem)
    }

    val padding = if(selected) 4.dp else 6.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(image)
            }
            .padding(padding)
            .combinedClickable(
                onLongClick = {
                    onLongClick()
                },
                onClick = { onClick(image) }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = image.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.full_logo),
                error = painterResource(id = R.drawable.logo)
            )
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


@Composable
fun FavoriteVideosList(
    videos: List<VideoXX>,
    onClick: (VideoXX) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<VideoXX>) -> Unit,
    cancelAction: Boolean
){



    val selectedVideos = remember {
        mutableStateListOf<VideoXX>()
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

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .height(900.dp)){
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
                items(
                    videos,
                    key = {
                     it._id
                    }
                ){ video ->
                    val isSelected = selectedVideos.contains(video)
                    FavoriteVideoItem(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteVideoItem(
    video: VideoXX,
    onClick: (VideoXX) -> Unit,
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
        model = video.videoUrl,
        imageLoader = imageLoader,
        error = painterResource(id = R.drawable.full_logo),
        placeholder = painterResource(id = R.drawable.get_started)
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


fun downloadMultipleVideos (videoUrls: List<String>, context:Context) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    for ((index, videoUrl) in videoUrls.withIndex()) {
        val request = DownloadManager.Request(Uri.parse(videoUrl))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setTitle("Video Downloaded")
            .setDescription("Downloading video ${index + 1}....")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "video_${index+1}.mp4")

        downloadManager.enqueue(request)
    }
}

fun shareMultipleOnlineVideos(context: Context, videoUrls: List<String>){

   downloadMultipleVideos(videoUrls,context)
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
                shareIntent.type = "video/*"
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(downloadedFileUris))
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(Intent.createChooser(shareIntent,"Share Videos"))
            }
        }

    }

    context.registerReceiver(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}

fun downloadAndShareMultipleVideos(
    videoUrls: List<String>,
    context: Context,
    showError : (Boolean,String) -> Unit
){

    val downloadManger = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadedFiles = mutableListOf<String>()

    for(videoUrl in videoUrls) {
        val fileName = "video_${videoUrls.indexOf(videoUrl)}.mp4"
        val request = DownloadManager.Request(Uri.parse(videoUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,fileName)
        request.setTitle("Video Download ")

        val downloadId = downloadManger.enqueue(request)

        object : AsyncTask<Unit,Unit,Boolean>() {
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
                            downloadedFiles.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+fileName)
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
                    shareIntent.type = "video/*"
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(downloadedFiles.map { Uri.parse(it) }))
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(shareIntent,"Share Videos"))
                } else{
                    showError(true,"cannot share Video")
                }
            }

        }.execute()

    }

}



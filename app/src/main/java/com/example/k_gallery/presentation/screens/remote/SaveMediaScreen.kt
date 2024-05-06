package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.sharp.ForwardToInbox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoAddRequest
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.Image
import com.example.k_gallery.data.dataSources.api.models.ImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.ImageObject
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
import com.example.k_gallery.data.dataSources.api.models.ImageX
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.SentImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.dataSources.api.models.Video
import com.example.k_gallery.data.dataSources.api.models.VideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.VideoObject
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import com.example.k_gallery.data.dataSources.api.models.VideoX
import com.example.k_gallery.presentation.screens.local.ImageItem
import com.example.k_gallery.presentation.screens.local.VideoItem
import com.example.k_gallery.presentation.screens.local.VideoPlayer
import com.example.k_gallery.presentation.screens.local.performSaveMultipleVideosToAccount
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class, DelicateCoroutinesApi::class,
    DelicateCoroutinesApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SaveMediaScreen(
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

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userViewModel.getSavedImages(email)
        userViewModel.getSavedVideos(email)
    }


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
        mutableStateListOf<Image>()
    }

    val selectedVideos = remember {
        mutableStateListOf<Video>()
    }
    var deletionResult by remember { mutableStateOf(false) }
    var deletionConfirmation by remember { mutableStateOf(false) }

    var favoriteResult by remember { mutableStateOf(false) }
    var favoriteConfirmation by remember { mutableStateOf(false) }


    var deletionResult2 by remember { mutableStateOf(false) }
    var deletionConfirmation2 by remember { mutableStateOf(false) }

    var favoriteResult2 by remember { mutableStateOf(false) }
    var favoriteConfirmation2 by remember { mutableStateOf(false) }

    var saveImageHandler by remember { mutableStateOf(false) }

    var saveVideoHandler by remember { mutableStateOf(false) }

    var saveMultipleImageHandler by remember { mutableStateOf(false) }

    var saveMultipleVideoHandler by remember { mutableStateOf(false) }


    val savedImagesList by userViewModel.savedImages.observeAsState()
    val savedVideosList by userViewModel.savedVideos.observeAsState()


    val deleteMultipleSavedVideo by userViewModel.deleteMultipleSavedVideos.observeAsState()
    val addMultipleSavedVideosToFavorite by userViewModel.addMultipleFavoriteVideos.observeAsState()

    val deleteMultipleSavedImage by userViewModel.deleteMultipleSavedImages.observeAsState()
    val addMultipleSavedImageToFavorite by userViewModel.addMultipleFavoriteImages.observeAsState()

    val saveImageResponse by userViewModel.saveImageResponse.observeAsState()
    val saveVideoResponse by userViewModel.saveVideoResponse.observeAsState()
    val saveMultipleImageResponse by userViewModel.saveMultipleImageResponse.observeAsState()
    val saveMultipleVideoResponse by userViewModel.saveMultipleVideoResponse.observeAsState()


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

    var isLoading by remember {
        mutableStateOf(false)
    }


    val forwardImageResponse by userViewModel.sendMultipleImageResponse.observeAsState()
    val forwardVideoResponse by userViewModel.sendMultipleVideoResponse.observeAsState()




    var isSendMediaOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isSendImageOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isSendVideoOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var caption by remember {
        mutableStateOf("")
    }

    val sheetState = rememberModalBottomSheetState()

    val sheetState1 = rememberModalBottomSheetState()

    val sheetState2 = rememberModalBottomSheetState()

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedImageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedVideoUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { imgUri->
            selectedImageUri = imgUri
            if(selectedImageUri != null){
                isSendMediaOpen = false
                isSendImageOpen = true
            }
        }
    )

    val singleVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { vidUri->
            selectedVideoUri = vidUri
            if(selectedVideoUri != null){
                isSendMediaOpen = false
                isSendVideoOpen = true
            }
        }
    )

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { imgUris->
            selectedImageUris = imgUris
            if(selectedImageUris.isNotEmpty()){
                isSendMediaOpen = false
                isSendImageOpen = true
            }
        }
    )

    val multipleVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { vidUris->
            selectedVideoUris = vidUris
            if (selectedVideoUris.isNotEmpty()){
                isSendMediaOpen = false
                isSendVideoOpen = true
            }
        }
    )

    val getMultipleImageContents = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()){ uris ->
        selectedImageUris = uris
        if(selectedImageUris.isNotEmpty()){
            isSendMediaOpen = false
            isSendImageOpen = true
        }
    }

    val getMultipleVideoContents = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()){ uris ->
        selectedVideoUris = uris
        if (selectedVideoUris.isNotEmpty()){
            isSendMediaOpen = false
            isSendVideoOpen = true
        }
    }

    val color = if(caption.isNotEmpty()) Blue else Color.Red


    var selectedImagesUrl : List<String>
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){}


    var selectedVideoUrls : List<String>

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
           if (!isSendMediaOpen){
               FloatingActionButton(
                   containerColor = Blue,
                   onClick = {
                       isSendMediaOpen = true
                   },
                   modifier = Modifier
                       .padding(16.dp)
                       .offset(y = ((-56).dp))
               ) {
                   Icon(
                       imageVector = Icons.Filled.Save,
                       contentDescription = null,
                       tint = Color.White
                   )
               }
           }
        },
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
                            favoriteConfirmation2 = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp, 16.dp, 0.dp, 0.dp)
        ) {
            item {
                ChoiceSelectionBoxes(
                    onFirstClick = {
                        currentComposable = composable1

                    },
                    onSecondClick = {
                        currentComposable = composable2

                    },
                    firstText = "Saved Images",
                    secondText = "Saved Videos",
                    firstSelectedIcon = Icons.Filled.Image,
                    firstUnselectedIcon = Icons.Outlined.Image,
                    secondSelectedIcon = Icons.Filled.VideoLibrary,
                    secondUnselectedIcon = Icons.Outlined.VideoLibrary
                )
            }
            item {
                Box(Modifier.fillMaxSize()) {
                    if (currentComposable == composable1) {

                        when (savedImagesList) {
                            is Resource.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            is Resource.Error -> {
                                val errorMessage =
                                    (savedImagesList as Resource.Error<SavedImages>).message
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
                                val savedImages =
                                    (savedImagesList as Resource.Success<SavedImages>).data?.images


                                SavedImageList(
                                    images = savedImages!!,
                                    onClick = {
                                        val imageIndex = savedImages.indexOf(it).toString()
                                        anotherNavController.navigate(NavHelper.ViewAllSavedImageScreen.route + "/$imageIndex" + "/$email")
                                    },
                                    selected = selected1,
                                    selectedChange = { selected1 = !selected1 },
                                    enabled = selected1,
                                    selectedImagesChange = { images ->
                                        selectedImages.clear()
                                        selectedImages.addAll(images)
                                    },
                                    cancelAction = cancel1
                                )
                            }

                            else -> {}
                        }


                    } else if (currentComposable == composable2) {

                        when (savedVideosList) {
                            is Resource.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }

                            is Resource.Error -> {
                                val errorMessage =
                                    (savedVideosList as Resource.Error<SavedVideos>).message
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
                                val savedVideos =
                                    (savedVideosList as Resource.Success<SavedVideos>).data!!.videos

                                SavedVideosList(
                                    videos = savedVideos,
                                    onClick = {
                                        val videoIndex = savedVideos.indexOf(it).toString()
                                        anotherNavController.navigate(NavHelper.ViewAllSavedVideoScreen.route + "/$videoIndex" + "/$email")
                                    },
                                    selected = selected2,
                                    selectedChange = {
                                        selected2= !selected2
                                    },
                                    enabled = selected2,
                                    selectedImagesChange = { videos ->
                                        selectedVideos.clear()
                                        selectedVideos.addAll(videos)
                                    },
                                    cancelAction = cancel2
                                )

                                FloatingActionButton(
                                    onClick = { isSendMediaOpen = true },
                                    modifier = Modifier.align(
                                        Alignment.BottomEnd
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

    }

        if(isSendMediaOpen){
            ModalBottomSheet(
                onDismissRequest = { isSendMediaOpen = false },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        IconButton(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.AddAPhoto,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save Image", color = Blue)
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                singleVideoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                    ) {
                        IconButton(onClick = {
                            singleVideoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Videocam,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save Video", color = Blue)
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                getMultipleImageContents.launch("image/*")
                            }
                    ) {
                        IconButton(onClick = {
                            getMultipleImageContents.launch("image/*")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Collections,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save Multiple Image", color = Blue)
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                getMultipleVideoContents.launch("video/*")
                            }
                    ) {
                        IconButton(onClick = {
                            getMultipleVideoContents.launch("video/*")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.VideoLibrary,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save Multiple Video", color = Blue)
                    }
                }
            }
        }

        if(isSendImageOpen){
            isSendVideoOpen = false
            ModalBottomSheet(
                onDismissRequest = { isSendImageOpen = false },
                sheetState = sheetState1
            ) {
                Box(modifier = Modifier.fillMaxSize()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        item {
                        selectedImageUri?.let { uri ->
                            Box(modifier = Modifier.fillMaxWidth()){
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = {
                                        selectedImageUri = null
                                        isSendImageOpen = false
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Blue
                                    ),
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Cancel,
                                        contentDescription = null,
                                        tint = Blue
                                    )
                                }
                            }
                        }
                        }

                     if(selectedImageUris.isNotEmpty()){
                         itemsIndexed(selectedImageUris){index, uri ->
                             Box(modifier = Modifier.fillMaxWidth()) {
                                 AsyncImage(
                                     model = uri,
                                     contentDescription = null,
                                     modifier = Modifier.fillMaxWidth(),
                                     contentScale = ContentScale.Crop
                                 )

                                 IconButton(
                                     onClick = {
                                         selectedImageUris = selectedImageUris.toMutableList().apply {
                                             removeAt(index)
                                         }
                                         isSendImageOpen = false
                                     },
                                     colors = IconButtonDefaults.iconButtonColors(
                                         containerColor = Color.Transparent,
                                         contentColor = Blue
                                     ),
                                     modifier = Modifier.align(Alignment.TopEnd)
                                 ) {
                                     Icon(
                                         imageVector = Icons.Outlined.Cancel,
                                         contentDescription = null,
                                         tint = Blue
                                     )
                                 }
                             }
                         }
                     }
                    }

                    if (selectedImageUri != null){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = caption,
                                onValueChange ={ caption = it } ,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(16.dp),
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = null)
                                },
                                placeholder = {
                                    Text(text = "Caption")
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = color,
                                    unfocusedBorderColor = Color.Black
                                ),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {

                                    if (caption.isNotEmpty()){
                                        userViewModel.saveSingleImage(email,selectedImageUri!!,caption, context)
                                        saveImageHandler = true
                                    } else {
                                        Toast.makeText(context,"Caption is empty", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null
                                )
                            }
                        }
                    } else if(selectedImageUris.isNotEmpty()){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = caption,
                                onValueChange ={ caption = it } ,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(16.dp),
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = null)
                                },
                                placeholder = {
                                    Text(text = "Caption")
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = color,
                                    unfocusedBorderColor = Color.Black
                                ),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {
                                    if (caption.isNotEmpty()){
                                            userViewModel.saveMultipleImage(email,selectedImageUris,context, caption)
                                            saveMultipleImageHandler = true
                                    } else {
                                        Toast.makeText(context,"Caption is empty", Toast.LENGTH_SHORT).show()
                                    }

                                      }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                }
            }
        }




        if(isSendVideoOpen){
            ModalBottomSheet(
                onDismissRequest = { isSendVideoOpen = false },
                sheetState = sheetState2
            ) {
                Box(modifier = Modifier.fillMaxSize()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        item {
                          selectedVideoUri?.let { uri ->
                              Box(modifier = Modifier.fillMaxWidth().height(600.dp)){
                                  VideoPlayer(uri, isVisible = {

                                  })
                                  IconButton(
                                      onClick = {
                                          selectedVideoUri = null
                                      },
                                      colors = IconButtonDefaults.iconButtonColors(
                                          containerColor = Color.Transparent,
                                          contentColor = Blue
                                      ),
                                      modifier = Modifier.align(Alignment.TopEnd)
                                  ) {
                                      Icon(
                                          imageVector = Icons.Outlined.Cancel,
                                          contentDescription = null,
                                          tint = Blue
                                      )
                                  }
                              }
                          }
                        }

                        itemsIndexed(selectedVideoUris){index, uri ->
                            Box(modifier = Modifier.fillMaxWidth().height(600.dp)) {
                                VideoPlayer(
                                    uri, isVisible = {

                                }
                                )

                                IconButton(
                                    onClick = {
                                        selectedVideoUris = selectedVideoUris.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Blue
                                    ),
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Cancel,
                                        contentDescription = null,
                                        tint = Blue
                                    )
                                }
                            }
                        }
                    }

                    if (selectedVideoUri != null){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = caption,
                                onValueChange ={ caption = it } ,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(16.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ClosedCaption,
                                        contentDescription = null
                                    )
                                },
                                placeholder = {
                                    Text(text = "Caption")
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = color,
                                    unfocusedBorderColor = Color.Black
                                ),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {
                                    if (caption.isNotEmpty()){
                                        userViewModel.saveSingleVideo(email, selectedVideoUri!!,context,caption)
                                        saveVideoHandler = true
                                    } else {
                                        Toast.makeText(context,"Caption is empty", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null,
                                    tint = color
                                )
                            }
                        }
                    } else if(selectedVideoUris.isNotEmpty()){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = caption,
                                onValueChange ={ caption = it } ,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(16.dp),
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.ClosedCaption, contentDescription = null)
                                },
                                placeholder = {
                                    Text(text = "Caption")
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = color,
                                    unfocusedBorderColor = Color.Black
                                ),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {
                                    if (caption.isNotEmpty()){
                                        userViewModel.saveMultipleVideo(email,selectedVideoUris,context, caption)
                                        saveMultipleVideoHandler = true
                                    } else {
                                        Toast.makeText(context,"Caption is empty", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null,
                                    tint = color
                                )
                            }
                        }
                    }

                }
            }
        }



        if (saveVideoHandler) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (saveVideoResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (saveVideoResponse as Resource.Error<User>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    saveVideoHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( saveVideoResponse as Resource.Success<User>).data
                    Toast.makeText(context,"Videos Saved Successfully", Toast.LENGTH_LONG).show()
                    saveVideoHandler = false
                    isSendVideoOpen = false
                    userViewModel.getSavedVideos(email)
                    caption = ""
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

        if (saveImageHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (saveImageResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (saveImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    saveImageHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( saveImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    saveImageHandler = false
                    isSendImageOpen = false
                    userViewModel.getSavedImages(email)
                    caption = ""
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


        if (saveMultipleVideoHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (saveMultipleVideoResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (saveMultipleVideoResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    saveMultipleVideoHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( saveMultipleVideoResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    saveMultipleVideoHandler = false
                    isSendVideoOpen = false
                    userViewModel.getSavedVideos(email)
                    caption = ""
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

        if (saveMultipleImageHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (saveMultipleImageResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (saveMultipleImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    saveMultipleImageHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( saveMultipleImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    saveMultipleImageHandler = false
                    isSendImageOpen = false
                    userViewModel.getSavedImages(email)
                    caption = ""
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



        if (deletionConfirmation){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedImages.size} Images",
                desc = "Are you sure want to delete these images? ",
                deletedAccepted = {
                    val ids = selectedImages.map {
                        it._id
                    }
                    val request = ImageDeleteRequest(
                        email = email,
                        ids = ids
                    )
                    userViewModel.deleteMultipleSavedImage(request)
                    deletionConfirmation = false
                    deletionResult = true
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
                }
            ) {
                favoriteConfirmation = false
            }
        }

        if (favoriteResult) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (addMultipleSavedImageToFavorite) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (addMultipleSavedImageToFavorite as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    favoriteResult = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( addMultipleSavedImageToFavorite as Resource.Success<Message>).data
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

        if (deletionConfirmation2){
            com.example.k_gallery.presentation.screens.local.ShowDeleteConfirmationALertDialog(
                title = "Delete ${selectedVideos.size} Videos",
                desc = "Are you sure want to delete these Videos? ",
                deletedAccepted = {
                    val ids = selectedVideos.map {
                        it._id
                    }
                    val request = VideoDeleteRequest(
                        email = email,
                        ids = ids
                    )
                    userViewModel.deleteMultipleSavedVideo(request)
                    deletionConfirmation2 = false
                    deletionResult2 = true
                }
            ) {
                deletionConfirmation2 = false
            }
        }

        if (deletionResult2) {
            cancel2 = !cancel2
            selected2 = !selected2
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


        if (favoriteConfirmation2){
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
                    favoriteConfirmation2 = false
                    favoriteResult2 = true
                }
            ) {
                favoriteConfirmation2 = false
            }
        }

        if (favoriteResult2) {
            cancel2 = !cancel2
            selected2 = !selected2
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (addMultipleSavedVideosToFavorite) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (addMultipleSavedVideosToFavorite as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    favoriteResult2 = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( addMultipleSavedVideosToFavorite as Resource.Success<Message>).data
                    Toast.makeText(context,success!!.message, Toast.LENGTH_LONG).show()
                    favoriteResult2 = false
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
                    isSendImageOpen = false
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
                    isSendImageOpen = false
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
            if(isLoading) {
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
fun ChoiceSelectionBoxes(
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    firstText:String,
    secondText: String,
    firstSelectedIcon: ImageVector,
    firstUnselectedIcon: ImageVector,
    secondSelectedIcon: ImageVector,
    secondUnselectedIcon:ImageVector
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
fun SavedImageList(
    images: List<Image>,
    onClick: (Image) -> Unit,
    selected: Boolean,
    selectedChange: (Boolean) -> Unit,
    enabled: Boolean,
    selectedImagesChange: (MutableList<Image>) -> Unit,
    cancelAction: Boolean
){
    val selectedImages = remember {
        mutableStateListOf<Image>()
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

                    SavedImageItem(
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
fun SavedImageItem(
    image: Image,
    onClick: (Image) -> Unit,
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
fun SavedVideosList(
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
                    SavedVideoItem(
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
fun SavedVideoItem(
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




data class TopNavigationItem(
    val title:String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val action: () -> Unit
)
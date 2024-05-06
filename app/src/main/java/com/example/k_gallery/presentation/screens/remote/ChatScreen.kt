package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibleForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.BorderClear
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.LightbulbCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.sharp.ArrowRight
import androidx.compose.material.icons.sharp.ForwardToInbox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.ChatMessage
import com.example.k_gallery.data.dataSources.api.models.ChatMessageList
import com.example.k_gallery.data.dataSources.api.models.ChatMessageX
import com.example.k_gallery.data.dataSources.api.models.Image
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SearchedUser
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import com.example.k_gallery.presentation.screens.local.VideoPlayer
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun ChatScreen (
    navController: NavController,
    fromEmail: String,
    toEmail: String,
    userViewModel: UserViewModel,
    ){
    LaunchedEffect(Unit){
        userViewModel.getChatMessages(fromEmail, toEmail)
    }

    val chatsList by userViewModel.chatMessageList.observeAsState()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var emailToSearch by remember {
        mutableStateOf("")
    }

    val userSearched by userViewModel.userSearchedResponse.observeAsState()

    val context = LocalContext.current

    var sendSingleImageHandler by remember { mutableStateOf(false) }

    var sendSingleVideoHandler by remember { mutableStateOf(false) }

    var forwardImageHandler by remember { mutableStateOf(false) }

    var forwardVideoHandler by remember { mutableStateOf(false) }

    var isSearchUser by remember { mutableStateOf(false) }

    val chatUsers by userViewModel.chatItemList.observeAsState()

    var showProfileImage by remember {
        mutableStateOf(false)
    }

    val sendSingleImageResponse by userViewModel.sendSingleImageResponse.observeAsState()
    val sendSingleVideoResponse by userViewModel.sendSingleVideoResponse.observeAsState()
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

    var forwardCaption by remember {
        mutableStateOf("")
    }

    val sheetState = rememberModalBottomSheetState()

    val sheetState1 = rememberModalBottomSheetState()

    val sheetState2 = rememberModalBottomSheetState()

    val sheetState3 = rememberModalBottomSheetState()


    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
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

    val color = if(caption.isNotEmpty()) Blue else Color.Red



    var isSelected by remember {
        mutableStateOf(false)
    }

    val selectedImages = remember {
        mutableStateListOf<String>()
    }

    val selectedVideos = remember {
        mutableStateListOf<String>()
    }

    val selectedImagesToDelete = remember {
        mutableStateListOf<String>()
    }

    val selectedVideosToDelete = remember {
        mutableStateListOf<String>()
    }

    var selectedImagesToDeleteFromMeSize by remember {
        mutableIntStateOf(0)
    }

    var selectedVideosToDeleteFromMeSize by remember {
        mutableIntStateOf(0)
    }

    var cancel1 by remember {
        mutableStateOf(false)
    }

    val selectedMessage = remember {
        mutableStateListOf<ChatMessageX>()
    }

    var userImg by remember {
        mutableStateOf<User?>(null)
    }

    LaunchedEffect(true){
        userImg = userViewModel.getUserDetails(toEmail)
    }

    Scaffold (
        floatingActionButton = {
           if (!isSendMediaOpen){
               FloatingActionButton(
                   containerColor = Blue,
                   onClick = {
                       isSendMediaOpen = true
                   },
                   modifier = Modifier
                       .padding(8.dp)
               ) {
                   Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null)
               }
           }
        },
         topBar = {
            if(isSelected){
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Blue ,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    title = {
                        Text(
                            text = "${selectedMessage.size} Messages Selected",
                            color = Color.White,
                        )
                    },
                    actions = {

                        IconButton(
                            onClick = {
                             Toast.makeText(context,"${selectedImagesToDeleteFromMeSize == selectedImagesToDelete.size} to delete",Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {
                            userViewModel.getChatItemList(fromEmail)
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
                            isSelected = !isSelected
                            cancel1 = !cancel1
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
            } else{
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Blue ,
                        titleContentColor = Color.White
                    ),
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            AsyncImage(
                                model = userImg?.user?.imageUrl ?: "djdhdhdhdh",
                                contentDescription = "user image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .padding(8.dp)
                                    .clickable { },
                                placeholder = painterResource(id = R.drawable.full_logo),
                                error = painterResource(id = R.drawable.logo),
                            )
                            Text(
                                text = if (toEmail == fromEmail) "$toEmail(me)" else toEmail,
                                color = Color.White,
                                modifier = Modifier.padding(8.dp),
                                fontSize = 15.sp
                            )
                        }
                    } ,
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp()}) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    ,
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
                            onDismissRequest = {expanded = false},
                            modifier = Modifier.background(Blue)
                        ) {

                            DropdownMenuItem(
                                text = { Text(text = "Active", color  = Color.White) },
                                onClick = {
                                },
                                trailingIcon = {
                                    Icon(imageVector = Icons.Default.LightbulbCircle, contentDescription = null)
                                }
                            )


                        }
                    }
                )
            }
         },


             ){
        when(chatsList){
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
                val chatsListMessage = (chatsList as Resource.Success<ChatMessageList>).data?.chatMessages
                ChatList(
                    messages = chatsListMessage!!,
                    navController,
                    isSelected = isSelected,
                    cancelAction = cancel1,
                    selectedMessages = { messages ->
                        selectedMessage.clear()
                        selectedImages.clear()
                        selectedVideos.clear()
                        selectedVideosToDelete.clear()
                        selectedVideosToDelete.clear()
                        selectedImagesToDeleteFromMeSize = 0
                        selectedVideosToDeleteFromMeSize = 0
                        selectedMessage.addAll(messages)
                        selectedMessage.forEach { msg ->
                            if (msg.isImage){
                                if (!msg.isSenderDeleted || !msg.isReceiverDeleted){
                                    selectedImages.add(msg.url)
                                }
                            } else if(msg.isVideo) {
                                if (!msg.isSenderDeleted || !msg.isReceiverDeleted) {
                                    selectedVideos.add(msg.url)
                                }
                            }
                        }

                        selectedMessage.forEach { msg ->
                            if (msg.isImage){
                                    selectedImagesToDelete.add(msg.url)
                                if (msg.isFromMe){
                                    selectedImagesToDeleteFromMeSize++
                                }
                            } else if(msg.isVideo) {
                                    selectedVideosToDelete.add(msg.url)
                                if (msg.isFromMe){
                                    selectedVideosToDeleteFromMeSize++
                                }
                            }
                        }
                        val capt = if (selectedMessage.isNotEmpty()) selectedMessage[selectedMessage.size-1].caption ?: ".." else "should not happen"
                                       forwardCaption = capt
                                       },
                    selectChange = {
                        isSelected = !isSelected
                    },
                    context = context

                )
            }

            is Resource.Error -> {
                isLoading = false
                val errMessage = (chatsList as Resource.Error<ChatMessageList>).message
                Toast.makeText(context,errMessage,Toast.LENGTH_SHORT).show()
            }

            else -> {

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
                        Text(text = "Send Image", color = Blue)
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
                        Text(text = "Send Video", color = Blue)
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
                                        userViewModel.sendSingleImage(fromEmail,toEmail, selectedImageUri!!,caption,context)
                                        sendSingleImageHandler = true
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
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .height(600.dp)){
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
                                        userViewModel.sendSingleVideo(fromEmail,toEmail, selectedVideoUri!!,context, caption)
                                        sendSingleVideoHandler = true
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



        if (sendSingleVideoHandler) {
            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (sendSingleVideoResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (sendSingleVideoResponse as Resource.Error<User>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    sendSingleVideoHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( sendSingleVideoResponse as Resource.Success<User>).data
                    Toast.makeText(context,"Videos Saved Successfully", Toast.LENGTH_LONG).show()
                    sendSingleVideoHandler = false
                    isSendVideoOpen = false
                    userViewModel.getChatMessages(fromEmail, toEmail)
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

        if (sendSingleImageHandler) {

            var isLoading3 by remember {
                mutableStateOf(false)
            }
            when (sendSingleImageResponse) {
                is Resource.Error -> {
                    isLoading3 = false
                    val err = (sendSingleImageResponse as Resource.Error<Message>).message
                    Toast.makeText(context,err!!, Toast.LENGTH_LONG).show()
                    sendSingleImageHandler = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( sendSingleImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,"Image Sent Successfully", Toast.LENGTH_LONG).show()
                    sendSingleImageHandler = false
                    isSendImageOpen = false
                    userViewModel.getChatMessages(fromEmail, toEmail)
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
                    isSelected = !isSelected
                    cancel1 = !cancel1
                    isSearchUser = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( forwardImageResponse as Resource.Success<Message>).data
                    Toast.makeText(context,"Message Forwarded Successfully", Toast.LENGTH_LONG).show()
                    forwardImageHandler = false
                    isSendImageOpen = false
                    userViewModel.getChatMessages(fromEmail, toEmail)
                    caption = ""
                    isSelected = !isSelected
                    cancel1 = !cancel1
                    isSearchUser = false
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
                    isSelected = !isSelected
                    cancel1 = !cancel1
                    isSearchUser = false
                }

                is Resource.Success -> {
                    isLoading3 = false
                    val success = ( forwardVideoResponse as Resource.Success<Message>).data
                    Toast.makeText(context,"Message Forwarded Successfully", Toast.LENGTH_LONG).show()
                    forwardVideoHandler = false
                    isSendImageOpen = false
                    userViewModel.getChatMessages(fromEmail, toEmail)
                    caption = ""
                    isSelected = !isSelected
                    cancel1 = !cancel1
                    isSearchUser = false
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
                            Text(text = "Forward Media", style = MaterialTheme.typography.titleMedium)
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

                                    if (userGotten!!.email == fromEmail){
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .clickable {
                                                    isSearchUser = false

                                                    if (selectedImages.isNotEmpty()) {
                                                        val request = ImageSendRequest(
                                                            selectedImages,
                                                            fromEmail,
                                                            userGotten.email,
                                                            caption
                                                        )
                                                        userViewModel.sendMultipleImage(request)
                                                    }

                                                    if (selectedVideos.isNotEmpty()) {
                                                        val request = VideoSendRequest(
                                                            selectedVideos,
                                                            fromEmail,
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
                                                                selectedImages,
                                                                fromEmail,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleImage(request)
                                                        }

                                                        if (selectedVideos.isNotEmpty()) {
                                                            val request = VideoSendRequest(
                                                                selectedVideos,
                                                                fromEmail,
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
                                                    if (selectedImages.isNotEmpty()) {
                                                        val request = ImageSendRequest(
                                                            selectedImages,
                                                            fromEmail,
                                                            userGotten.email,
                                                            forwardCaption
                                                        )
                                                        userViewModel.sendMultipleImage(request)
                                                    }

                                                    if (selectedVideos.isNotEmpty()) {
                                                        val request = VideoSendRequest(
                                                            selectedVideos,
                                                            fromEmail,
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
                                                                selectedImages,
                                                                fromEmail,
                                                                userGotten.email,
                                                                forwardCaption
                                                            )
                                                            userViewModel.sendMultipleImage(request)
                                                        }

                                                        if (selectedVideos.isNotEmpty()) {
                                                            val request = VideoSendRequest(
                                                                selectedVideos,
                                                                fromEmail,
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
                                        if (selectedImages.isNotEmpty()) {
                                            val request = ImageSendRequest(
                                                selectedImages,
                                                fromEmail,
                                                chat.email,
                                                forwardCaption
                                            )
                                            userViewModel.sendMultipleImage(request)
                                        }

                                        if (selectedVideos.isNotEmpty()) {
                                            val request = VideoSendRequest(
                                                selectedVideos,
                                                fromEmail,
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
    }


}


@Composable
fun ChatList(
    messages: List<ChatMessageX>,
    navController: NavController,
    isSelected: Boolean,
    selectedMessages: (MutableList<ChatMessageX>) -> Unit,
    cancelAction: Boolean,
    selectChange: (Boolean) -> Unit,
    context: Context
){


    val selectedMessage = remember {
        mutableStateListOf<ChatMessageX>()
    }

    var isSelectedEnabled by remember {
        mutableStateOf(isSelected)
    }

    LaunchedEffect(cancelAction){
        selectedMessage.clear()
        isSelectedEnabled = !isSelectedEnabled
    }



    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(bottom = 42.dp, top = 50.dp, start = 16.dp, end = 16.dp)
        ){
//            val sortedMessages = messages.filter { message ->
//                 !message.isFromMe &&  !message.isSenderDeleted
//            }
//
//            val anotherSortedMessage = sortedMessages.distinctBy {
//                it
//            }
            items(
                messages,
                key = {
                    it.id
                }
            ){ message ->
                val selected = selectedMessage.contains(message)
                ChatItem(
                    message,
                    navController,
                    onLongClick = {
                        if (!selected){
                            selectedMessage.add(message)
                        }
                        isSelectedEnabled = !isSelectedEnabled
                        selectChange(isSelectedEnabled)
                        selectedMessages(selectedMessage)
                    },
                    onClick = {
                              if (isSelected){
                                  if (!selected){
                                      selectedMessage.add(message)
                                  } else{
                                     selectedMessage.remove(message)
                                      if (selectedMessage.size == 0){
                                          isSelectedEnabled = !isSelectedEnabled
                                          selectChange(isSelectedEnabled)
                                      }
                                  }
                                  selectedMessages(selectedMessage)
                              } else{
                                  val text ="sent by ${message.timeStamp}"
                                  Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
                              }
                    },
                    isSelected = selectedMessage.contains(message)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    message: ChatMessageX,
    navController: NavController,
    onLongClick : () -> Unit,
    onClick : () -> Unit,
    isSelected: Boolean
){
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = message.url,
        imageLoader = imageLoader,
        error = painterResource(id = R.drawable.full_logo),
        placeholder = painterResource(id = R.drawable.full_logo)
    )

    val align = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val selectedColor = if(isSelected) Color.LightGray else Color.Transparent
    Box(
       modifier = Modifier
           .fillMaxWidth()
           .padding(vertical = 13.dp)
           .combinedClickable(
               onLongClick = {
                   onLongClick()
               },
               onClick = {
                   onClick()
               }
           )
           .background(color = selectedColor),
       contentAlignment = align
   ) {
        if (message.isReceiverDeleted || message.isSenderDeleted) {
            if (!message.isFromMe){
                Column(getBackground(message.isFromMe,onClick,onLongClick)) {
                    Box(modifier = Modifier.fillMaxWidth()){
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "this message was deleted",
                                color = Color.White,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        } else {
       Column(
           modifier = getBackground(message.isFromMe,onClick, onLongClick)
       ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                if (message.isImage){
                    AsyncImage(
                        model = message.url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable {
                                if (!isSelected) {
                                    val encodedUrl = Uri.encode(message.url)
                                    navController.navigate(NavHelper.ViewOneImageScreen.route + "/${encodedUrl}" + "/${message.sender}" + "/${message.caption}" + "/${message.timeStamp}")
                                }
                            }
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.full_logo),
                        error = painterResource(id = R.drawable.full_logo)
                    )
                } else if(message.isVideo){
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable {
                                    if (!isSelected) {
                                        val encodedUrl = Uri.encode(message.url)
                                        navController.navigate(NavHelper.ViewOneVideoScreen.route + "/${encodedUrl}" + "/${message.sender}" + "/${message.caption}" + "/${message.timeStamp}")
                                    }
                                }
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { val  encodedUrl = Uri.encode(message.url)
                                navController.navigate(NavHelper.ViewOneVideoScreen.route+"/${encodedUrl}"+"/${message.sender}"+"/${message.caption}"+"/${message.timeStamp}")  },
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
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val checkIcons = if (message.delivered && message.read){
                    Icons.Default.DoneAll
                } else if (message.delivered){
                    Icons.Default.DoneAll
                } else {
                    Icons.Default.Check
                }

                val iconsTint = if (message.delivered && message.read){
                    Red
                } else if (message.delivered){
                    Color.White
                } else {
                    Color.White
                }
                Text(
                    text = message.caption,
                    fontSize = 16.sp,
                    modifier = Modifier

                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = message.timeStamp.slice(0..20),
                        modifier = Modifier
                            .padding(8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        imageVector = checkIcons,
                        contentDescription = null,
                        tint = iconsTint
                    )
                }

            }
        }
       }
   }
}


@OptIn(ExperimentalFoundationApi::class)
private fun getBackground(
    isFromMe: Boolean,
    onClick: () -> Unit,
    onLongClick : () -> Unit
) : Modifier {
    return Modifier
        .background(
            color = if (isFromMe) Color.DarkGray else Blue,
            shape = RoundedCornerShape(
                topStart = if (isFromMe) 0.dp else 16.dp,
                topEnd = if (isFromMe) 16.dp else 0.dp,
                bottomStart = if (isFromMe) 16.dp else 0.dp,
                bottomEnd = if (isFromMe) 0.dp else 16.dp,
            )
        )
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .fillMaxWidth(0.7f)
        .combinedClickable(
            onClick = {
                onClick()
            },
            onLongClick = {
                onLongClick()
            }
        )

}
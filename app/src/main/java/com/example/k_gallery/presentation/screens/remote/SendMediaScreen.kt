package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.Coil
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatItem
import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.SearchedUser
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMediaScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    anotherNavController: NavController
) {

    LaunchedEffect(Unit){
        userViewModel.getChatItemList(email)
    }

    val context = LocalContext.current

    val chatsList by userViewModel.chatItemList.observeAsState()

    var showProfileImage by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isLoading2 by remember {
        mutableStateOf(false)
    }

    var isLoading3 by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var isSearchUser by rememberSaveable {
        mutableStateOf(false)
    }

    var searchedUser by remember {
        mutableStateOf<List<SearchedUser>>(emptyList())
    }
    var emailToSearch by remember {
        mutableStateOf("")
    }
    val userSearched by userViewModel.userSearchedResponse.observeAsState()
    val addSearchedUserResponse by userViewModel.addSearchedUserResponse.observeAsState()
    val deleteSearchedUserResponse by userViewModel.deleteSearchedUserResponse.observeAsState()
    val getAllSearchedUserResponse by userViewModel.searchedUsersResponse.observeAsState()

    val sheetState = rememberModalBottomSheetState()
    val color = if(emailToSearch.isNotEmpty()) Blue else Color.Red

Scaffold (
    modifier = Modifier.fillMaxSize(),
    floatingActionButton = {
       if (!isSearchUser){
           FloatingActionButton(containerColor = Blue,onClick = {
               isSearchUser = true
               userViewModel.getAllSearchedUser(email)
           }, modifier = Modifier
               .padding(16.dp)
               .offset(y = ((-56).dp))
           ) {
               Icon(
                   imageVector = Icons.Filled.Chat,
                   contentDescription = null,
                   tint = Color.White
               )
           }
       }
    },

        ) {
    Box(Modifier.fillMaxSize()) {
      Box(modifier = Modifier.fillMaxWidth()){
          DropdownMenu(
              expanded = expanded,
              onDismissRequest = {expanded = false},
              modifier = Modifier
                  .background(Blue)
                  .align(Alignment.TopEnd),
              offset = DpOffset(x = 182.dp, y = 72.dp)
          ) {
              DropdownMenuItem(
                  text = { Text(text = "Sent Images", color  = Color.White) },
                  onClick = {
                      anotherNavController.navigate(NavHelper.SentImageScreen.route+"/$email")
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
                  text = { Text(text = "Sent Videos", color  = Color.White) },
                  onClick = {
                      anotherNavController.navigate(NavHelper.SentVideoScreen.route+"/$email")
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
                  text = { Text(text="Received Images",color  = Color.White)},
                  onClick = {
                      anotherNavController.navigate(NavHelper.ReceivedImageScreen.route+"/$email")
                  },
                  trailingIcon = {
                      Icon(
                          imageVector = Icons.Default.PictureInPictureAlt,
                          contentDescription = null,
                          tint = Color.White
                      )
                  }
              )
              DropdownMenuItem(
                  text = { Text(text="Received Videos",color  = Color.White)},
                  onClick = {
                      anotherNavController.navigate(NavHelper.ReceivedVideoScreen.route+"/$email")
                  },
                  trailingIcon = {
                      Icon(
                          imageVector = Icons.Default.FeaturedVideo,
                          contentDescription = null,
                          tint = Color.White
                      )
                  }
              )

          }
      }
       Row(
           Modifier
               .fillMaxWidth()
               .padding(8.dp)
               .padding(bottom = 16.dp)){
           Box(modifier = Modifier.fillMaxWidth()) {
               Text(
                   text = "Chats",
                   color = Blue,
                   fontSize = 24.sp,
                   fontWeight = FontWeight.Bold,
                   modifier = Modifier
                       .padding(26.dp)
                       .align(Alignment.TopCenter)
               )


               IconButton(
                   onClick = { expanded = true },
                   modifier = Modifier
                       .align(Alignment.TopEnd)
                       .padding(26.dp)
               ) {
                   Icon(
                       imageVector = Icons.Default.MoreVert,
                       contentDescription = null
                   )
               }


           }
       }
        if(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp)
                .padding(8.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                bottom = 52.dp
            )
        ){
            when(chatsList){
                is Resource.Success -> {
                    isLoading = false
                    val listOfChats = (chatsList as Resource.Success<ChatItems>).data!!.chatItems

                    items(
                        listOfChats,
                        key = {
                            it.email
                        }
                    ){ chat ->
                        ChatSenderItem(
                            chat = chat,
                            clickAction = {
                                anotherNavController.navigate(NavHelper.ChatMessageScreen.route+"/${email}"+"/${chat.email}")
                            },
                            userViewModel = userViewModel,
                            showProfile = true
                        )
                       if (showProfileImage){
                           com.example.k_gallery.presentation.screens.remote.ImageDialog(imageUrl = chat.url) {

                           }
                       }
                    }


                }

                is Resource.Loading -> {
                    item {
                        isLoading = true

                    }
                }

                is Resource.Error -> {
                    isLoading = false
                    val errorMessage = (chatsList as Resource.Error<ChatItems>).message
                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show()
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
                    userViewModel.searchedUsersResponse.postValue(null)
                    userViewModel.userSearchedResponse.postValue(null)
                    userViewModel.addSearchedUserResponse.postValue(null)
                    userViewModel.deleteSearchedUserResponse.postValue(null)
                                },
                sheetState = sheetState
            ) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
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
                                        userViewModel.addSearchedUser(email,userGotten!!.email)
                                        userViewModel.getAllSearchedUser(email)
                                        once = false
                                    }

                                   if (userGotten!!.email == email){
                                       Row (
                                           verticalAlignment = Alignment.CenterVertically,
                                           modifier = Modifier
                                               .fillMaxWidth()
                                               .padding(8.dp)
                                               .clickable {
                                                   isSearchUser = false
                                                   anotherNavController.navigate(NavHelper.ChatMessageScreen.route + "/${email}" + "/${userGotten.email}")
                                               }
                                       ) {
                                           AsyncImage(
                                               model = userGotten.imageUrl,
                                               contentDescription = null,
                                               modifier = Modifier
                                                   .size(50.dp)
                                                   .clickable {
                                                       isSearchUser = false
                                                       anotherNavController.navigate(NavHelper.ChatMessageScreen.route + "/${email}" + "/${userGotten.email}")
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
                                                   anotherNavController.navigate(NavHelper.ChatMessageScreen.route + "/${email}" + "/${userGotten!!.email}")
                                               }
                                       ) {
                                           AsyncImage(
                                               model = userGotten.imageUrl,
                                               contentDescription = null,
                                               modifier = Modifier
                                                   .size(50.dp)
                                                   .clickable {
                                                       isSearchUser = false
                                                       anotherNavController.navigate(NavHelper.ChatMessageScreen.route + "/${email}" + "/${userGotten.email}")
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
                            when(addSearchedUserResponse){
                                is Resource.Error -> {
                                    val errMessage = (addSearchedUserResponse as Resource.Error<Message>).message
                                    Toast.makeText(context,errMessage,Toast.LENGTH_SHORT).show()
                                    userViewModel.addSearchedUserResponse.postValue(null)
                                    userViewModel.getAllSearchedUser(email)
                                }

                                is Resource.Success -> {
                                    val successMessage = (addSearchedUserResponse as Resource.Success<Message>).data?.message
                                    Toast.makeText(context,successMessage!!,Toast.LENGTH_SHORT).show()
                                    userViewModel.addSearchedUserResponse.postValue(null)
                                    userViewModel.getAllSearchedUser(email)
                                }

                                else -> {}
                            }

                            when(deleteSearchedUserResponse){
                                is Resource.Error -> {
                                    val errMessage = (deleteSearchedUserResponse as Resource.Error<Message>).message
                                    Toast.makeText(context,errMessage,Toast.LENGTH_SHORT).show()
                                    userViewModel.deleteSearchedUserResponse.postValue(null)
                                    userViewModel.getAllSearchedUser(email)
                                }

                                is Resource.Success -> {
                                    val successMessage = (deleteSearchedUserResponse as Resource.Success<Message>).data?.message
                                    Toast.makeText(context,successMessage!!,Toast.LENGTH_SHORT).show()
                                    userViewModel.deleteSearchedUserResponse.postValue(null)
                                    userViewModel.getAllSearchedUser(email)
                                }

                                else -> {}
                            }
                        }

                    }
                    when(getAllSearchedUserResponse){
                        is Resource.Success -> {
                            val allSearchedUser = (getAllSearchedUserResponse as Resource.Success<SearchedUser>).data!!.searchedUser

                            item{
                               Row(
                                   verticalAlignment = Alignment.CenterVertically,
                                   horizontalArrangement = Arrangement.Center,
                                   modifier = Modifier.fillMaxWidth()
                               ) {
                                   Text(text = "Recent")
                               }
                            }

                            items(
                                allSearchedUser,
                                key = {
                                    it._id
                                }
                            ){ searchedUser ->
                                Row (
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            isSearchUser = false
                                            anotherNavController.navigate(NavHelper.ChatMessageScreen.route + "/${email}" + "/${searchedUser.searchedEmail}")
                                        }
                                ) {
                                    val text1 = if (searchedUser.searchedEmail == email){
                                        if ( searchedUser.searchedEmail.length > 17)  searchedUser.searchedEmail.slice(0..17)+"..(me)" else  searchedUser.searchedEmail+"(me)"
                                    } else {
                                        if ( searchedUser.searchedEmail.length > 17)  searchedUser.searchedEmail.slice(0..17)+".." else  searchedUser.searchedEmail
                                    }
                                    Text(
                                        text = text1 ,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = searchedUser.lastAction.slice(0..17),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    IconButton(onClick = {
                                        userViewModel.deleteSearchedUserResponse.postValue(null)
                                        userViewModel.deleteSearchedUser(searchedUser._id)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = null,
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

                        is Resource.Error -> {
                            val errMessage = (getAllSearchedUserResponse as Resource.Error<SearchedUser>).message
                            item {
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
                        }

                        is Resource.Loading -> {
                            item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                        else -> {}
                    }


                }
            }
        }

    }
}
}

@Composable
fun ChatSenderItem(
    chat: ChatItem,
    clickAction: () -> Unit,
    userViewModel: UserViewModel,
    showProfile: Boolean
){
    var userImg by remember {
        mutableStateOf<User?>(null)
    }

    var showProfileImage by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(true){
        userImg = userViewModel.getUserDetails(chat.email)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable {
                clickAction()
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = userImg?.user?.imageUrl ?: chat.url  ,
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    showProfileImage = true
                }
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            placeholder = painterResource(id = R.drawable.full_logo),
            error = painterResource(id = R.drawable.logo)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = if (chat.email.length > 16) chat.email.slice(0..16)+"..." else chat.email,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
          Row {
              Icon(
                  imageVector = if (chat.type == "video") Icons.Default.VideoFile else Icons.Default.Image,
                  contentDescription = null
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = chat.caption, style = MaterialTheme.typography.bodySmall)
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if(chat.date.length > 20) chat.date.slice(0..20)  else chat.date,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
        )

        if (showProfile){
            if (showProfileImage){
                ImageDialog(imageUrl = userImg?.user?.imageUrl ?: chat.url) {
                    showProfileImage = false
                }
            }
        }
    }
}



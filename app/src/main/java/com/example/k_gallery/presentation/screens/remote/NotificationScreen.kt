package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.ChatMessage
import com.example.k_gallery.data.dataSources.api.models.Notice
import com.example.k_gallery.data.dataSources.api.models.NotificationResponse
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
 navController: NavController,
 userViewModel: UserViewModel,
 email: String
) {
 LaunchedEffect(Unit){
  userViewModel.getUserNotification(email)
 }

 val notificationList by userViewModel.notifications.observeAsState()

 var isLoading by remember {
  mutableStateOf(false)
 }

 var expanded by remember {
  mutableStateOf(false)
 }

 val context = LocalContext.current


 Scaffold(
  modifier = Modifier.fillMaxSize(),
  topBar = {
   TopAppBar(
    title = {
     Text(text = "Notifications")
    },
    navigationIcon = {
     IconButton(onClick = { navController.navigateUp()}) {
      Icon(
       imageVector = Icons.Default.ArrowBackIosNew,
       contentDescription = null,
       tint = Blue
      )
     }
    }
   )

  }

 ) {

  when(notificationList){
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
    val notificationLists = (notificationList as Resource.Success<NotificationResponse>).data?.notices

      NotificationList(notifications = notificationLists!!){ notification ->
        when(notification.action){
         "open-image" ->{
          navController.navigate(NavHelper.HomeDashBoardScreenScreen.route )
         }
         "open-details" ->{
          navController.navigate(NavHelper.ProfileViewOrEditScreen.route + "/$email")
         }

         "open-chat" ->{
          navController.navigate(NavHelper.SentImageScreen.route + "/$email")
         }

         "open-favorite" ->{
          navController.navigate(NavHelper.HomeDashBoardScreenScreen.route )
         }
         "open-video" ->{
          navController.navigate(NavHelper.HomeDashBoardScreenScreen.route )
         }

         "open-settings" ->{
          navController.navigate(NavHelper.UserSettingsScreen.route + "/$email")
         }

         else -> {

         }

        }
      }
   }

   is Resource.Error -> {
    isLoading = false
    val errMessage = (notificationList as Resource.Error<NotificationResponse>).message
    Toast.makeText(context,errMessage, Toast.LENGTH_SHORT).show()
   }

   else -> {}
  }
 }
}


@Composable
fun NotificationList(notifications: List<Notice>, onClick: (Notice) -> Unit){
  LazyColumn(
   Modifier.fillMaxSize(),
   contentPadding = PaddingValues(top = 52.dp)
  ){
   items(
    notifications,
    key = {
    it._id
    }
   ){ notification ->
    NotificationCard(notice = notification){
      onClick(it)
    }
   }
  }
}


@Composable
fun NotificationCard(notice: Notice, onClick: (Notice) -> Unit) {
  Card(
   modifier = Modifier
    .fillMaxWidth()
    .padding(16.dp)
    .clickable {
     onClick(notice)
    },
   elevation = CardDefaults.cardElevation(4.dp)
  ) {
   Column (
    modifier = Modifier
     .fillMaxWidth()
     .padding(16.dp)
           ) {

    Row(
     modifier = Modifier.fillMaxWidth(),
     verticalAlignment = Alignment.CenterVertically
    ) {
     Image(
      painter = painterResource(id = R.drawable.full_logo),
      contentDescription = null,
      modifier = Modifier
       .size(25.dp)
       .clip(CircleShape)
       .border(1.dp, Color.Red, CircleShape)
     )
     Spacer(modifier = Modifier.width(4.dp))
     Text(
      text = notice.heading,
      style = MaterialTheme.typography.titleMedium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
     )
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
     text = notice.body,
     style = MaterialTheme.typography.bodyMedium,
     maxLines = 2,
     overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
     text = "Date: ${notice.date.slice(0..20)}",
     style = MaterialTheme.typography.bodySmall,
     color = Color.Gray
    )
   }
  }
}



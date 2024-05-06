package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.data.dataSources.api.models.Settings
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.UserPreferences
import com.example.k_gallery.presentation.util.UserSharedPrefManager
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserSettingsScreen(
    email: String,
    navController: NavController,
    userSharedViewModel: UserSharedViewModel,
    userViewModel: UserViewModel
){
    var darkMode by remember {
        mutableStateOf(false)
    }
    var notificationOn by remember {
        mutableStateOf(false)
    }
    var newsletter by remember {
        mutableStateOf(false)
    }
    var language by remember {
        mutableStateOf("")
    }

    var lastAction by remember {
        mutableStateOf("")
    }

    val getUserSettings by userViewModel.getUserSettings.observeAsState()
    val updateUserSettings by userViewModel.updateUserSettings.observeAsState()

    val user by  userSharedViewModel.userData.observeAsState()
    val imageUrl by remember {
        mutableStateOf(user!!.user.imageUrl)
    }

    val context = LocalContext.current

    val userPrefManager = remember {
        UserSharedPrefManager(context)
    }



    LaunchedEffect(Unit){
        userViewModel.getUserSettings(email)
    }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
          TopAppBar(
              title = {
                  Text(text = "My Settings")
              },
              navigationIcon = {
                  IconButton(onClick = {
                      navController.navigateUp()
                  }) {
                      Icon(
                          imageVector = Icons.Default.ArrowBackIos,
                          contentDescription = null,
                          tint = Blue
                      )
                  }
              },
              actions = {
                  AsyncImage(
                      model = imageUrl,
                      contentDescription = "Profile image",
                      modifier = Modifier
                          .clickable {
                              navController.navigate(NavHelper.ProfileViewOrEditScreen.route + "/$email")
                          }
                          .size(48.dp)
                          .clip(CircleShape),
                      placeholder = painterResource(id = R.drawable.full_logo),
                      error = painterResource(id = R.drawable.logo)
                  )
              }
          )
      }
  ) {
      LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(top = 40.dp)
      ){
         item{
             when(getUserSettings){
                 is Resource.Loading ->{
                     LoadingDialog()
                 }
                 is Resource.Error -> {
                     val errMessage = (getUserSettings as Resource.Error<Settings>).message
                     Toast.makeText(context,errMessage!!, Toast.LENGTH_LONG).show()
                 }

                 is Resource.Success -> {
                     val settings = (getUserSettings as Resource.Success<Settings>).data?.settings
                     settings?.let { settingsX ->
                         darkMode = settingsX.darkMode
                         language = settingsX.language
                         lastAction = settingsX.lastAction
                         notificationOn = settingsX.notificationOn
                         newsletter = settingsX.sendNewsLetter
                     }
                     Column (
                         modifier = Modifier
                             .fillMaxSize()
                             .padding(16.dp),
                         horizontalAlignment = Alignment.CenterHorizontally
                     ){
                         SettingsItem(name = "DarkMode") {
                             Switch(
                                 checked = darkMode,
                                 onCheckedChange = {
                                     darkMode = it
                                 }
                             )
                         }
                         SettingsItem(name = "Notifications") {
                             Switch(
                                 checked = notificationOn,
                                 onCheckedChange = {
                                     notificationOn = it
                                 }
                             )
                         }

                         SettingsItem(name = "Language") {
                             TextField(
                                 value = language,
                                 onValueChange = { language = it },
                                 prefix = {
                                     IconButton(onClick = {  }) {
                                         Icon(
                                             imageVector = Icons.Default.Language,
                                             contentDescription = null
                                         )
                                     }
                                 },
                                 modifier = Modifier.fillMaxWidth(0.7f),
                                 singleLine = true
                             )
                         }

                         SettingsItem(name = "NewsLetter") {
                             Switch(
                                 checked = newsletter,
                                 onCheckedChange = {
                                     newsletter = it
                                 }
                             )
                         }

                         SettingsItem(name = "LastAction") {
                             Text(text = if(lastAction.length >= 22) lastAction.slice(0..22) else lastAction)
                         }

                         Button(
                             onClick = {
                                 userViewModel.updateUserSettings(email,darkMode,language,notificationOn,newsletter)
                             }
                         ) {
                             Text(text = "Update Settings")
                         }

                         Spacer(modifier = Modifier.height(32.dp))
                         Button(
                             onClick = {
                                 logoutUser(userPrefManager,navController)
                             },
                             colors = ButtonDefaults.buttonColors(
                                 contentColor = Color.White,
                                 containerColor = Color.Red
                             )

                         ) {
                             Text(text = "Logout")
                         }

                     }
                 }

                 else -> {}
             }

             when(updateUserSettings){
                 is Resource.Loading ->{
                     LoadingDialog()
                 }
                 is Resource.Error -> {
                     val errMessage = (updateUserSettings as Resource.Error<Settings>).message
                     Toast.makeText(context,errMessage!!, Toast.LENGTH_LONG).show()
                 }

                 is Resource.Success -> {
                     val settings = (updateUserSettings as Resource.Success<Settings>).data?.settings
                     settings?.let { settingsX ->
                         darkMode = settingsX.darkMode
                         language = settingsX.language
                         lastAction = settingsX.lastAction
                         notificationOn = settingsX.notificationOn
                         newsletter = settingsX.sendNewsLetter

                         userSharedViewModel.userSettings.postValue(settingsX)
                     }

                     Toast.makeText(context,"User Settings Updated", Toast.LENGTH_LONG).show()


                 }

                 else -> {}
             }
         }
      }
  }
}

@Composable
fun SettingsItem(
      name: String,
      value: @Composable () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium
        )
        value()
    }
}




fun logoutUser(userSharedPrefManager: UserSharedPrefManager,navController: NavController) {
    val userPref = UserPreferences("","",false)
    userSharedPrefManager.setLoggedInPrefs(userPref)
    navController.navigate(NavHelper.HomeScreen.route){
        popUpTo(NavHelper.HomeScreen.route){ inclusive = true}
    }
}
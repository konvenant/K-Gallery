package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.k_gallery.R
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel
import com.example.k_gallery.ui.theme.Blue
import com.example.k_gallery.ui.theme.Milk
import com.example.k_gallery.ui.theme.Red
import okhttp3.internal.wait

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    userSharedViewModel: UserSharedViewModel,
    anotherNavController: NavController
) {

    val user = userSharedViewModel.userData.value

    var showProfileImage by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                Modifier
                    .fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blue)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Account",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
//                            .padding(8.dp)
                    )
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blue)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                        ) {
                    AsyncImage(
                        model = user!!.user.imageUrl,
                        contentDescription = user.user.email+"image",
                        modifier = Modifier
                            .clickable {
                                showProfileImage = true
                            }
                            .size(48.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.full_logo),
                        error = painterResource(id = R.drawable.logo)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = user.user.email,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = user.user.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            anotherNavController.navigate(NavHelper.ProfileViewOrEditScreen.route+"/$email")
                        },
                        Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "update",
                            tint = Color.White
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier
                    .padding(16.dp)
                    .background(Milk)
                    .clip(RoundedCornerShape(10.dp))
                ) {
                    ProfileItem(
                        icon = Icons.Filled.Person,
                        title = "Profile",
                        desc = "View or Edit Profile"
                    ) {
                        anotherNavController.navigate(NavHelper.ProfileViewOrEditScreen.route+"/$email")
                    }

                    ProfileItem(
                        icon = Icons.Filled.Notifications,
                        title = "Notifications",
                        desc = "View Notifications"
                    ) {
                        anotherNavController.navigate(NavHelper.NotificationScreen.route+"/$email")
                    }

                    ProfileItem(
                        icon = Icons.Filled.Settings,
                        title = "Settings",
                        desc = "change user settings"
                    ) {
                        anotherNavController.navigate(NavHelper.UserSettingsScreen.route + "/$email")
                    }

                    ProfileItem(
                        icon = Icons.Filled.HelpCenter,
                        title = "Help",
                        desc = "Reach out to us for help"
                    ) {

                    }

                }
            }
        }
        if (showProfileImage){
            ImageDialog(imageUrl = user!!.user.imageUrl) {
                showProfileImage = false
            }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    desc:String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable {
                onClick()
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(Modifier.fillMaxWidth()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column (modifier = Modifier.align(Alignment.Center)){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = desc, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onClick() }, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit
){
    AlertDialog(onDismissRequest = {
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                placeholder = painterResource(id = R.drawable.full_logo),
                error = painterResource(id = R.drawable.logo)
            )
        }
    }
}
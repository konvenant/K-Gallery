package com.example.k_gallery.presentation.screens.remote

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceScreen(
    navController: NavController
){

    Scaffold (
        modifier = Modifier.fillMaxSize()
            ){
       Box(
           modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colorScheme.background)
       ) {
           Image(
               painter = painterResource(id = R.drawable.get_started),
               contentDescription = null,
               modifier = Modifier.fillMaxSize(),
               contentScale = ContentScale.Crop
           )
           Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier
                   .fillMaxWidth()
                   .background(Color.Transparent)
                   .padding(16.dp)
                   .align(Alignment.BottomCenter)
           ) {
               Button(
                   modifier = Modifier
                       .padding(8.dp)
                       .fillMaxWidth(),
                   onClick = { navController.navigate(NavHelper.SignupScreen.route){
                       popUpTo(NavHelper.ChoiceScreen.route){
                           inclusive = true
                       }
                       launchSingleTop = true
                   } }
               ) {
                   Text(text = "Signup",modifier = Modifier
                       .padding(8.dp))
               }
               Button(
                   modifier = Modifier
                       .padding(8.dp)
                       .fillMaxWidth(),
                   onClick = { navController.navigate(NavHelper.LoginScreen.route){
                       popUpTo(NavHelper.ChoiceScreen.route){
                           inclusive = true
                       }
                       launchSingleTop = true
                   }
                   },
                   colors = ButtonDefaults.buttonColors(
                       containerColor = Red
                   )
               ) {
                   Text(
                       text = "Login",
                       modifier = Modifier
                           .padding(8.dp)
                   )
               }
           }
       }
    }
}

package com.example.k_gallery.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.k_gallery.R
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.ui.theme.Milk
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController
) {
    val isFirstTime = false
    LaunchedEffect(key1 = true){
        delay(3000)
        if (isFirstTime){
            navController.navigate(NavHelper.DetailsScreen.route){
                popUpTo(NavHelper.SplashScreen.route){
                    inclusive = true
                }
            }
        } else {
            navController.navigate(NavHelper.HomeScreen.route){
                popUpTo(NavHelper.SplashScreen.route){
                    inclusive = true
                }
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().background(Milk)
    ){
        Image(painter = painterResource(id = R.drawable.klogo), contentDescription = null)
    }
}

package com.example.k_gallery.presentation.graph

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.k_gallery.presentation.screens.remote.CompleteRegistrationScreen
import com.example.k_gallery.presentation.screens.remote.OnlineFavoriteScreen
import com.example.k_gallery.presentation.screens.remote.ProfileScreen
import com.example.k_gallery.presentation.screens.remote.RemoteDetailScreen
import com.example.k_gallery.presentation.screens.remote.SaveMediaScreen
import com.example.k_gallery.presentation.screens.remote.SendMediaScreen
import com.example.k_gallery.presentation.screens.remote.UserHomeScreen
import com.example.k_gallery.presentation.screens.remote.ViewOneImageScreen
import com.example.k_gallery.presentation.screens.remote.ViewOneVideoScreen
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeNavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    email: String,
    anotherNavController: NavController,
    userSharedViewModel: UserSharedViewModel
){
    NavHost(
        navController = navController,
        startDestination = NavHelper.HomeOnlineScreen.route,
        route = NavHelper.HomeDashBoardScreenScreen.route
    ){
        composable(NavHelper.HomeOnlineScreen.route){
            UserHomeScreen(navController,userViewModel,email,anotherNavController)
        }
        composable(NavHelper.SavedMediaScreen.route){
            SaveMediaScreen(navController,userViewModel,email,anotherNavController)
        }
        composable(NavHelper.SendMediaScreen.route){
            SendMediaScreen(navController,userViewModel, email,anotherNavController)
        }
        composable(NavHelper.OnlineFavoriteScreen.route){
            OnlineFavoriteScreen(navController,userViewModel, email,anotherNavController)
        }
        composable(NavHelper.UserScreen.route){
            ProfileScreen(navController,userViewModel, email,userSharedViewModel,anotherNavController)
        }
    }
}

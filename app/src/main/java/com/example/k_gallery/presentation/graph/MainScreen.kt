package com.example.k_gallery.presentation.graph

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.k_gallery.presentation.screens.local.AllVideosScreen
import com.example.k_gallery.presentation.screens.local.DetailScreen
import com.example.k_gallery.presentation.screens.local.FavoriteScreen
import com.example.k_gallery.presentation.screens.local.FoldersScreen
import com.example.k_gallery.presentation.screens.local.HomeScreen
import com.example.k_gallery.presentation.screens.local.ImageScreen
import com.example.k_gallery.presentation.screens.local.MultipleVideoScreen
import com.example.k_gallery.presentation.screens.local.OnboardingScreen
import com.example.k_gallery.presentation.screens.local.PhotosImageScreen
import com.example.k_gallery.presentation.screens.local.PhotosScreen
import com.example.k_gallery.presentation.screens.local.SplashScreen
import com.example.k_gallery.presentation.screens.local.VideoHomeScreen
import com.example.k_gallery.presentation.screens.local.VideoScreen
import com.example.k_gallery.presentation.screens.local.VideosInFolderScreen
import com.example.k_gallery.presentation.screens.remote.ChoiceScreen
import com.example.k_gallery.presentation.screens.remote.CompleteForgotPasswordPasswordScreen
import com.example.k_gallery.presentation.screens.remote.CompleteRegistrationScreen
import com.example.k_gallery.presentation.screens.remote.ForgotPassword
import com.example.k_gallery.presentation.screens.remote.GetStartedScreen
import com.example.k_gallery.presentation.screens.remote.HomeDashboardScreen
import com.example.k_gallery.presentation.screens.remote.LoginScreen
import com.example.k_gallery.presentation.screens.remote.SignupScreen
import com.example.k_gallery.presentation.screens.remote.UpdateUserDetailsScreen
import com.example.k_gallery.presentation.screens.remote.UpdateUserPasswordScreen
import com.example.k_gallery.presentation.screens.remote.VerifyEmailScreen
import com.example.k_gallery.presentation.screens.remote.VerifyForgotPasswordToken
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    lifecycleOwner: LifecycleOwner
){
    val navController = rememberNavController()

    val sharedViewModel: UserSharedViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = NavHelper.AuthScreens.route
    ){
        authNavGraph(navController, sharedViewModel,lifecycleOwner)
        composable(NavHelper.HomeDashBoardScreenScreen.route){
            HomeDashboardScreen(navController,sharedViewModel,lifecycleOwner)
        }
    }
}

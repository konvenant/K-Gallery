package com.example.k_gallery.presentation.graph

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
import com.example.k_gallery.presentation.screens.remote.LoginScreen
import com.example.k_gallery.presentation.screens.remote.SignupScreen
import com.example.k_gallery.presentation.screens.remote.UpdateUserDetailsScreen
import com.example.k_gallery.presentation.screens.remote.UpdateUserPasswordScreen
import com.example.k_gallery.presentation.screens.remote.VerifyEmailScreen
import com.example.k_gallery.presentation.screens.remote.VerifyForgotPasswordToken
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel

@RequiresApi(Build.VERSION_CODES.R)
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    sharedViewModel: UserSharedViewModel,
    lifecycleOwner: LifecycleOwner
){
    navigation(
        route = NavHelper.AuthScreens.route,
        startDestination = NavHelper.SplashScreen.route
    ){
        composable(route = NavHelper.SplashScreen.route){
            SplashScreen(navController = navController)
        }

        composable(route = NavHelper.DetailsScreen.route+"/{name}"+"/{path}"+"/{dateTaken}"+"/{size}"){ backEntry ->
            val name = backEntry.arguments?.getString("name") ?: ""
            val path = backEntry.arguments?.getString("path") ?: ""
            val decodedPath = Uri.decode(path)
            val dateTaken = backEntry.arguments?.getString("dateTaken") ?: ""
            val size = backEntry.arguments?.getString("size") ?: ""
            DetailScreen(navController = navController, name, decodedPath, dateTaken, size)
        }

        composable(route = NavHelper.FoldersScreen.route+"/{folderId}"+"/{folderName}"){ backEntry ->
            val folderId = backEntry.arguments?.getString("folderId") ?: ""
            val folderName = backEntry.arguments?.getString("folderName") ?: ""
            FoldersScreen(navController = navController, folderId = folderId, folderName = folderName)
        }

        composable(route = NavHelper.ImageScreen.route+"/{imageIndex}"+"/{folderId}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            val folderId = backEntry.arguments?.getString("folderId") ?: ""
            ImageScreen(navController = navController,imageIndex,folderId)
        }

        composable(route = NavHelper.HomeScreen.route){
            HomeScreen(navController = navController,lifecycleOwner,sharedViewModel)
        }

        composable(route = NavHelper.VideoHomeScreen.route){
            VideoHomeScreen(navController = navController,lifecycleOwner,sharedViewModel)
        }

        composable(route = NavHelper.PhotosScreen.route){
            PhotosScreen(navController = navController)
        }

        composable(route = NavHelper.PhotosImageScreen.route+"/{imageIndex}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            PhotosImageScreen(navController = navController,imageIndex)
        }

        composable(route = NavHelper.VideosInFolderScreen.route+"/{folderId}"+"/{folderName}") { backEntry ->
            val folderId = backEntry.arguments?.getString("folderId") ?: ""
            val folderName = backEntry.arguments?.getString("folderName") ?: ""
            VideosInFolderScreen(navController = navController, folderId,folderName)
        }

        composable(route = NavHelper.VideoScreen.route+"/{videoIndex}"+"/{folderId}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            val folderId = backEntry.arguments?.getString("folderId") ?: ""
            VideoScreen(navController = navController,videoIndex,folderId)
        }

        composable(route = NavHelper.AllVideosScreen.route){
            AllVideosScreen(navController)
        }

        composable(route = NavHelper.MultipleVideoScreen.route+"/{videoIndex}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            MultipleVideoScreen(navController, videoIndex )
        }

        composable(route = NavHelper.FavoriteScreen.route){
            FavoriteScreen(navController)
        }

        composable(route = NavHelper.OnboardingScreen.route){
            OnboardingScreen(navController)
        }

        composable(route= NavHelper.GetStartedScreen.route){
            GetStartedScreen(navController)
        }

        composable(route = NavHelper.ChoiceScreen.route){
            ChoiceScreen(navController)
        }

        composable(route = NavHelper.LoginScreen.route){
            LoginScreen(navController,sharedViewModel)
        }

        composable(route = NavHelper.SignupScreen.route){
            SignupScreen(navController)
        }

        composable(route = NavHelper.ForgotPasswordScreen.route){
            ForgotPassword(navController)
        }
        composable(route = NavHelper.VerifyEmailScreen.route + "/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            VerifyEmailScreen(navController,email)
        }

        composable(route = NavHelper.VerifyForgotPasswordTokenScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            VerifyForgotPasswordToken(navController,email)
        }

        composable(route = NavHelper.CompleteRegistrationScreen.route + "/{email}"){backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            CompleteRegistrationScreen(navController,email,sharedViewModel)
        }

        composable(route = NavHelper.CompleteForgotPasswordScreen.route + "/{email}"){backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            CompleteForgotPasswordPasswordScreen(navController,email)
        }

        composable(route = NavHelper.UpdateUserDetailScreen.route){
            UpdateUserDetailsScreen(navController)
        }

        composable(route= NavHelper.UpdateUserPasswordScreen.route){
            UpdateUserPasswordScreen(navController)
        }

    }
}

package com.example.k_gallery.presentation.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.k_gallery.presentation.util.NavHelper


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(

){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
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
            HomeScreen(navController = navController)
        }

        composable(route = NavHelper.VideoHomeScreen.route){
            VideoHomeScreen(navController = navController)
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
    }
}

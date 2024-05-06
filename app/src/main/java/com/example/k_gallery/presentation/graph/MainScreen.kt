package com.example.k_gallery.presentation.graph

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.k_gallery.presentation.screens.local.DetailScreen
import com.example.k_gallery.presentation.screens.local.DetailScreen2
import com.example.k_gallery.presentation.screens.remote.ChatScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllSavedVideosScreen
import com.example.k_gallery.presentation.screens.remote.HomeDashboardScreen
import com.example.k_gallery.presentation.screens.remote.NotificationScreen
import com.example.k_gallery.presentation.screens.remote.ReceivedImageScreen
import com.example.k_gallery.presentation.screens.remote.ReceivedVideoScreen
import com.example.k_gallery.presentation.screens.remote.RemoteDetailScreen
import com.example.k_gallery.presentation.screens.remote.SentImageScreen
import com.example.k_gallery.presentation.screens.remote.SentVideoScreen
import com.example.k_gallery.presentation.screens.remote.UserSettingsScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllFavoriteImageScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllFavoriteVideosScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllReceivedImageScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllReceivedVideosScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllSavedImageScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllSentImagesScreen
import com.example.k_gallery.presentation.screens.remote.ViewAllSentVideosScreen
import com.example.k_gallery.presentation.screens.remote.ViewOneImageScreen
import com.example.k_gallery.presentation.screens.remote.ViewOneVideoScreen
import com.example.k_gallery.presentation.screens.remote.ViewOrEditProfileScreen
import com.example.k_gallery.presentation.util.NavHelper
import com.example.k_gallery.presentation.viewmodel.UserSharedViewModel
import com.example.k_gallery.presentation.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    lifecycleOwner: LifecycleOwner
){
    val navController = rememberNavController()

    val userViewModel: UserViewModel = hiltViewModel()

    val sharedViewModel: UserSharedViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = NavHelper.AuthScreens.route
    ){
        authNavGraph(navController, sharedViewModel,lifecycleOwner)
        composable(NavHelper.HomeDashBoardScreenScreen.route){
            HomeDashboardScreen(navController,sharedViewModel,lifecycleOwner,userViewModel)
        }

        composable(NavHelper.NotificationScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            NotificationScreen(navController, userViewModel, email)
        }
        composable(route = NavHelper.ViewOneImageScreen.route+"/{imageUrl}"+"/{desc}"+"/{id}"+"/{date}"){ backEntry ->
            val imageUrl = backEntry.arguments?.getString("imageUrl") ?: ""
            val decodedImageUrl = Uri.decode(imageUrl)
            val desc = backEntry.arguments?.getString("desc") ?: ""
            val id = backEntry.arguments?.getString("id") ?: ""
            val date = backEntry.arguments?.getString("date") ?: ""

            ViewOneImageScreen(navController,decodedImageUrl,desc,id,date,userViewModel,sharedViewModel)
        }

        composable(route = NavHelper.ViewOneVideoScreen.route+"/{videoUrl}"+"/{desc}"+"/{id}"+"/{date}"){ backEntry ->
            val videoUrl = backEntry.arguments?.getString("videoUrl") ?: ""
            val decodedImageUrl = Uri.decode(videoUrl)
            val desc = backEntry.arguments?.getString("desc") ?: ""
            val id = backEntry.arguments?.getString("id") ?: ""
            val date = backEntry.arguments?.getString("date") ?: ""

            ViewOneVideoScreen(navController,decodedImageUrl,desc,id,date,userViewModel,sharedViewModel)
        }

        composable(route = NavHelper.RemoteDetailsScreen.route+"/{name}"+"/{desc}"+"/{date}"){ backEntry ->
            val name = backEntry.arguments?.getString("name") ?: ""
            val desc = backEntry.arguments?.getString("desc") ?: ""

            val date = backEntry.arguments?.getString("date") ?: ""

            RemoteDetailScreen(navController, name, date, desc )
        }


        composable(route = NavHelper.ViewAllSavedImageScreen.route+"/{imageIndex}"+"/{email}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllSavedImageScreen(navController, imageIndex, email, userViewModel )
        }

        composable(route = NavHelper.ViewAllSavedVideoScreen.route+"/{videoIndex}"+"/{email}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllSavedVideosScreen(navController, videoIndex, email, userViewModel)
        }

        composable(route = NavHelper.ViewAllSentImageScreen.route+"/{imageIndex}"+"/{email}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllSentImagesScreen(navController, imageIndex, email, userViewModel)
        }

        composable(route = NavHelper.ViewAllSentVideoScreen.route+"/{videoIndex}"+"/{email}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllSentVideosScreen(navController,videoIndex, email, userViewModel)
        }

        composable(route = NavHelper.ViewAllReceivedImageScreen.route+"/{imageIndex}"+"/{email}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllReceivedImageScreen(navController, imageIndex, email, userViewModel)
        }

        composable(route = NavHelper.ViewAllReceivedVideoScreen.route+"/{videoIndex}"+"/{email}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllReceivedVideosScreen(navController, videoIndex, email, userViewModel)
        }


        composable(route = NavHelper.ViewAllFavoriteImageScreen.route+"/{imageIndex}"+"/{email}"){ backEntry ->
            val imageIndex = backEntry.arguments?.getString("imageIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllFavoriteImageScreen(navController, imageIndex, email, userViewModel)
        }

        composable(route = NavHelper.ViewAllFavoriteVideoScreen.route+"/{videoIndex}"+"/{email}"){ backEntry ->
            val videoIndex = backEntry.arguments?.getString("videoIndex") ?: ""
            val email = backEntry.arguments?.getString("email") ?: ""
            ViewAllFavoriteVideosScreen(navController, videoIndex, email,userViewModel)
        }

        composable(route = NavHelper.ChatMessageScreen.route+"/{fromEmail}"+"/{toEmail}"){ backEntry ->
            val fromEmail = backEntry.arguments?.getString("fromEmail") ?: ""
            val toEmail = backEntry.arguments?.getString("toEmail") ?: ""
            ChatScreen(navController, fromEmail, toEmail, userViewModel)
        }

        composable(route = NavHelper.DetailsScreen2.route+"/{name}"+"/{path}"+"/{dateTaken}"+"/{size}"){ backEntry ->
            val name = backEntry.arguments?.getString("name") ?: ""
            val path = backEntry.arguments?.getString("path") ?: ""
            val decodedPath = Uri.decode(path)
            val dateTaken = backEntry.arguments?.getString("dateTaken") ?: ""
            val size = backEntry.arguments?.getString("size") ?: ""
            DetailScreen2(navController = navController, name, decodedPath, dateTaken, size)
        }


        composable(route = NavHelper.SentImageScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            SentImageScreen(navController,email,userViewModel)
        }

        composable(route = NavHelper.SentVideoScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            SentVideoScreen(navController,email,userViewModel)
        }

        composable(route = NavHelper.ReceivedImageScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            ReceivedImageScreen(navController,email,userViewModel)
        }

        composable(route = NavHelper.ReceivedVideoScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            ReceivedVideoScreen(navController,email,userViewModel)
        }

        composable(route = NavHelper.ProfileViewOrEditScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
          ViewOrEditProfileScreen(
              navController,
              userViewModel,
              email,
              sharedViewModel
          )
        }

        composable(route = NavHelper.UserSettingsScreen.route+"/{email}"){ backEntry ->
            val email = backEntry.arguments?.getString("email") ?: ""
            UserSettingsScreen(
                email,
                navController,
                sharedViewModel,
                userViewModel
            )
        }


    }
}

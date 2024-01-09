package com.example.k_gallery.presentation.util

sealed class NavHelper(var route:String){
    object SplashScreen: NavHelper("splash")
    object DetailsScreen: NavHelper("detail")
    object FoldersScreen: NavHelper("folders")
    object ImageScreen: NavHelper("image")
    object HomeScreen: NavHelper("home")
    object PhotosScreen: NavHelper("photos")
    object PhotosImageScreen: NavHelper("photosImage")
    object VideoHomeScreen: NavHelper("videoHomeScreen")
    object VideosInFolderScreen: NavHelper("videosInFolderScreen")
    object VideoScreen: NavHelper("videoScreen")
    object AllVideosScreen: NavHelper("videoScreen")
    object MultipleVideoScreen: NavHelper("videoScreen")
    object FavoriteScreen: NavHelper("favoriteScreen")
}

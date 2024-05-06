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
    object OnboardingScreen: NavHelper("onboardingScreen")
    object GetStartedScreen: NavHelper("getStartedScreen")
    object ChoiceScreen: NavHelper("choiceScreen")
    object LoginScreen: NavHelper("loginScreen")
    object SignupScreen: NavHelper("signupScreen")
    object VerifyEmailScreen: NavHelper("verifyEmailScreen")
    object VerifyForgotPasswordTokenScreen: NavHelper("verifyForgotPasswordTokenScreen")
    object UpdateUserPasswordScreen: NavHelper("updateUserPasswordScreen")
    object UpdateUserDetailScreen:NavHelper("updateUserDetailsScreen")
    object CompleteRegistrationScreen:NavHelper("completeRegistrationScreen")
    object HomeDashBoardScreenScreen: NavHelper("homeDashBoardScreen")
    object ForgotPasswordScreen: NavHelper("forgotPassword")
    object CompleteForgotPasswordScreen: NavHelper("completeForgotPassword")
    object HomeOnlineScreen: NavHelper("homeOnlineScreen")
    object SavedMediaScreen: NavHelper("savedMediaScreen")
    object SendMediaScreen: NavHelper("sendMediaScreen")
    object OnlineFavoriteScreen: NavHelper("OnlineFavoriteScreen")
    object UserScreen: NavHelper("UserScreen")
    object AuthScreens: NavHelper("AuthScreens")

    object ViewOneImageScreen: NavHelper("ViewOneImage")

    object ViewOneVideoScreen: NavHelper("ViewOneVideo")

    object ViewAllSavedImageScreen: NavHelper("ViewAllSavedImages")

    object ViewAllSavedVideoScreen: NavHelper("ViewAllSavedVideos")

    object ViewAllSentImageScreen: NavHelper("ViewAllSentImages")

    object ViewAllSentVideoScreen: NavHelper("ViewAllSentVideos")

    object ViewAllReceivedImageScreen: NavHelper("ViewAllReceivedImage")

    object ViewAllReceivedVideoScreen: NavHelper("ViewAllReceivedVideos")
    object ViewAllFavoriteImageScreen: NavHelper("ViewAllFavoriteImage")
    object ViewAllFavoriteVideoScreen: NavHelper("ViewFavoriteVideo")
    object RemoteDetailsScreen: NavHelper("RemoteDetails")
    object DetailsScreen2: NavHelper("secondDetail")

    object ChatMessageScreen: NavHelper("chatMessageScreen")
    object SentImageScreen: NavHelper("sentImageScreen")
    object SentVideoScreen: NavHelper("sentVideoScreen")
    object ReceivedImageScreen: NavHelper("receivedImageScreen")
    object ReceivedVideoScreen: NavHelper("receivedVideoScreen")
    object NotificationScreen : NavHelper("notificationScreen")
    object ProfileViewOrEditScreen : NavHelper("viewOrEditProfileScreen")

    object UserSettingsScreen: NavHelper("userSettingScreen")



}

package com.example.k_gallery.data.repositories.remoteRepositories

import com.example.k_gallery.data.dataSources.api.RetrofitInstance
import com.example.k_gallery.data.dataSources.api.UserApi
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.ImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
import com.example.k_gallery.data.dataSources.api.models.SentImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.VideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val UserRetrofitInstance: UserApi
){

    suspend fun signUp(url:String,email:String,password:String) =
        UserRetrofitInstance.signUp(url, email, password)

    suspend fun login(url:String,email:String,password:String) =
        UserRetrofitInstance.login(url, email, password)

    suspend fun updateUser(url:String,email:String,name:String,phone:String,city: String,country: String) =
        UserRetrofitInstance.updateUser(url, email, name, phone, city, country)

    suspend fun verifyEmail(url:String,email:String,token:Int) =
        UserRetrofitInstance.verifyEmail(url, email, token)

    suspend fun verifyPassword(url:String,email:String,passwordToken:String) =
        UserRetrofitInstance.verifyPassword(url, email, passwordToken)

    suspend fun sendToken(url:String,email:String) =
        UserRetrofitInstance.sendToken(url, email)

    suspend fun forgotPassword(email: String) =
        UserRetrofitInstance.forgotPassword(email)

    suspend fun updatePassword(url:String,email:String,password: String) =
        UserRetrofitInstance.updatePassword(url, email, password)

    suspend fun updateImage(url:String,image: MultipartBody.Part) =
        UserRetrofitInstance.updateImage(url,image)

    suspend fun getUserNotification(email: String) =
        UserRetrofitInstance.getUserNotifications(email)

    suspend fun getUserNotificationCount(email: String) =
        UserRetrofitInstance.getUserNotificationCount(email)

    suspend fun getUserDetails(email: String) =
        UserRetrofitInstance.getUserDetails(email)


    suspend fun updateDeliveryState(email: String) =
        UserRetrofitInstance.updateDeliveryState(email)

    suspend fun saveImage(email: String,caption:String,image: MultipartBody.Part) =
        UserRetrofitInstance.saveImage(email,caption,image)

    suspend fun sendImage(fromEmail: String,toEmail: String,caption: String,image: MultipartBody.Part) =
        UserRetrofitInstance.sendImage(fromEmail, toEmail, caption, image)

    suspend fun saveVideo(email: String,caption:String,video: MultipartBody.Part) =
        UserRetrofitInstance.saveVideo(email,caption,video)

    suspend fun sendVideo(fromEmail: String,toEmail: String,caption: String,video: MultipartBody.Part) =
        UserRetrofitInstance.sendVideo(fromEmail, toEmail, caption, video)

    suspend fun saveMultipleImages(email: String,caption: String,images: List<MultipartBody.Part>) =
        UserRetrofitInstance.saveMultipleImage(email,caption, images )

    suspend fun sendMultipleImages(request: ImageSendRequest) =
        UserRetrofitInstance.sendMultipleImage(request)

    suspend fun saveMultipleVideo(email: String,caption: String,videos: List<MultipartBody.Part>) =
        UserRetrofitInstance.saveMultipleVideo(email,caption, videos)

    suspend fun sendMultipleVideo(request: VideoSendRequest) =
        UserRetrofitInstance.sendMultipleVideos(request)

    suspend fun getSavedImages(email: String) =
        UserRetrofitInstance.getSavedImages(email)

    suspend fun getSavedVideos(email: String) =
        UserRetrofitInstance.getSavedVideos(email)

    suspend fun deleteSavedImage(email: String,id:String) =
        UserRetrofitInstance.deleteSavedImage(email, id)

    suspend fun deleteSavedVideo(email: String, id:String) =
        UserRetrofitInstance.deleteSavedVideo(email, id)

    suspend fun deleteMultipleSavedImage(request: ImageDeleteRequest) =
        UserRetrofitInstance.deleteMultipleSavedImages(request)

    suspend fun deleteMultipleSavedVideo(request: VideoDeleteRequest) =
        UserRetrofitInstance.deleteMultipleSavedVideos(request)

    suspend fun getAllSentImages(email: String) =
        UserRetrofitInstance.getAllSentImages(email)

    suspend fun getAllSentVideo(email: String) =
        UserRetrofitInstance.getAllSentVideos(email)

    suspend fun getAllReceievedImage(email: String) =
        UserRetrofitInstance.getAllRecievedImages(email)

    suspend fun getAllReceievedVideos(email: String) =
        UserRetrofitInstance.getAllRecievedVideos(email)

    suspend fun getAllChatImages(fromEmail: String, toEmail: String) =
        UserRetrofitInstance.getAllChatImages(fromEmail, toEmail)

    suspend fun getAllChatVideos(fromEmail: String, toEmail: String) =
        UserRetrofitInstance.getAllChatVideos(fromEmail, toEmail)



    suspend fun deleteSentImage(fromEmail:String,toEmail:String,id: String) =
        UserRetrofitInstance.deleteSentImage(fromEmail, toEmail, id)

    suspend fun deleteSentVideo(fromEmail:String,toEmail:String,id: String) =
        UserRetrofitInstance.deleteSentVideo(fromEmail, toEmail, id)

    suspend fun deleteRecievedImage(fromEmail:String,toEmail:String,id: String) =
        UserRetrofitInstance.deleteReceievedImage(fromEmail, toEmail, id)

    suspend fun deleteRecievedVideo(fromEmail:String,toEmail:String,id: String) =
        UserRetrofitInstance.deleteReceievedVideo(fromEmail, toEmail, id)

    suspend fun deleteMultipleImages(request: SentImageDeleteRequest) =
        UserRetrofitInstance.deleteMultipleImage(request)

    suspend fun deleteMultipleVideos(request: SentVideoDeleteRequest) =
        UserRetrofitInstance.deleteMultipleVideos(request)

    suspend fun addFavoriteImage(email: String, imageUrl:String) =
        UserRetrofitInstance.addFavoriteImage(email, imageUrl)

    suspend fun addFavoriteVideo(email: String, videoUrl: String) =
        UserRetrofitInstance.addFavoriteVideo(email, videoUrl)

    suspend fun getAllFavoriteImages(email: String) =
        UserRetrofitInstance.getFavoriteImages(email)

    suspend fun getAllFavoriteVideos(email: String) =
        UserRetrofitInstance.getFavoriteVideos(email)

    suspend fun deleteFavoriteImage(email: String,id: String) =
        UserRetrofitInstance.deleteFavoriteImage(email, id)

    suspend fun deleteFavoriteVideo(email: String, id: String) =
        UserRetrofitInstance.deleteFavoriteVideo(email,id)

    suspend fun deleteMultipleFavoriteImage(request: FavoriteImageDeleteRequest) =
        UserRetrofitInstance.deleteMultipleFavoriteImage(request)

    suspend fun deleteMultipleFavoriteVideo(request: FavoriteVideoDeleteRequest) =
        UserRetrofitInstance.deleteMultipleFavoriteVideo(request)

    suspend fun addMultipleFavoriteImage(request: FavoriteImageAddRequest) =
        UserRetrofitInstance.addMultipleFavoriteImage(request)

    suspend fun addMultipleFavoriteVideos(request: FavoriteVideoAddRequest) =
        UserRetrofitInstance.addMultipleFavoriteVideo(request)

    suspend fun getUser(email: String) =
        UserRetrofitInstance.getUser(email)

    suspend fun getUserEmail(email: String) =
        UserRetrofitInstance.getUserEmail(email)

    suspend fun addSearchedUser(email: String, searchedEmail: String) =
        UserRetrofitInstance.addSearchedEmail(email, searchedEmail)

    suspend fun getAllSearchedEmail(email: String) =
        UserRetrofitInstance.getAllSearchedEmail(email)

    suspend fun deleteSearchedEmail(id: String) =
        UserRetrofitInstance.deleteSearchedEmail(id)

    suspend fun getUserSettings(email: String) =
        UserRetrofitInstance.getUserSettings(email)

    suspend fun updateSettings(
        email: String,
        darkMode: Boolean,
        language: String,
        notificationOn: Boolean,
        sendNewsLetter: Boolean
    ) =
        UserRetrofitInstance.updateSetting(email, darkMode, language, notificationOn, sendNewsLetter)


    suspend fun getChatItemList(email: String) =
        UserRetrofitInstance.getChatItemList(email)

    suspend fun getChatMessageList(email1: String, email2: String) =
        UserRetrofitInstance.getChatMessageList(email1, email2)
}

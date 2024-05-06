package com.example.k_gallery.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.BaseApplication
import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.ChatMessage
import com.example.k_gallery.data.dataSources.api.models.ChatMessageList
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.ImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
import com.example.k_gallery.data.dataSources.api.models.ImageX
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.NotificationCount
import com.example.k_gallery.data.dataSources.api.models.NotificationResponse
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.SearchedUser
import com.example.k_gallery.data.dataSources.api.models.SentImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.data.dataSources.api.models.SentVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.SentVideos
import com.example.k_gallery.data.dataSources.api.models.Settings
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.dataSources.api.models.VideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import com.example.k_gallery.data.dataSources.api.models.VideoX
import com.example.k_gallery.data.repositories.remoteRepositories.UserRepository
import com.example.k_gallery.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    app: Application
) : AndroidViewModel(app){
    val notifications : MutableLiveData<Resource<NotificationResponse>> = MutableLiveData()
    private lateinit var notificationResponse: NotificationResponse

    val notificationCount : MutableLiveData<Resource<NotificationCount>> = MutableLiveData()
    lateinit var notificationCountResponse: NotificationCount

    val savedImages : MutableLiveData<Resource<SavedImages>> = MutableLiveData()
    lateinit var savedImagesResponse: SavedImages

    val savedVideos : MutableLiveData<Resource<SavedVideos>> = MutableLiveData()
    lateinit var savedVideosResponse: SavedVideos

    val sentImages : MutableLiveData<Resource<SentImages>> = MutableLiveData()
    lateinit var sentImagesResponse: SentImages

    val sentVideos : MutableLiveData<Resource<SentVideos>> = MutableLiveData()
    lateinit var sentVideosResponse: SentVideos

    val receivedImages : MutableLiveData<Resource<SentImages>> = MutableLiveData()
    lateinit var receivedImagesResponse: SentImages

    val receivedVideos : MutableLiveData<Resource<SentVideos>> = MutableLiveData()
    lateinit var receivedVideosResponse: SentVideos

    val favoriteImages : MutableLiveData<Resource<FavoriteImages>> = MutableLiveData()
    lateinit var favoriteImagesResponse: FavoriteImages

    val favoriteVideos : MutableLiveData<Resource<FavoriteVideos>> = MutableLiveData()
    lateinit var favoriteVideosResponse: FavoriteVideos

    val chatItems : MutableLiveData<Resource<List<Chat>>> = MutableLiveData()

    val chatMessageItems : MutableLiveData<Resource<List<ChatMessage>>> = MutableLiveData()

    val chatItemList : MutableLiveData<Resource<ChatItems>> = MutableLiveData()

    val chatMessageList : MutableLiveData<Resource<ChatMessageList>> = MutableLiveData()


    val showFab: MutableLiveData<Boolean> = MutableLiveData()

    val deleteSavedImage: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteSavedVideo: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteSentImage: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteSentVideo: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteRecievedImage: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteRecievedVideo: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteFavoriteImage: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteFavoriteVideo: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleSavedVideos: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleSavedImages: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleSentImages: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleSentVideos: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleFavoriteImages: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteMultipleFavoriteVideos: MutableLiveData<Resource<Message>> = MutableLiveData()

    val addMultipleFavoriteImages: MutableLiveData<Resource<Message>> = MutableLiveData()

    val addMultipleFavoriteVideos: MutableLiveData<Resource<Message>> = MutableLiveData()

    val addFavoriteVideo: MutableLiveData<Resource<Message>> = MutableLiveData()

    val addFavoriteImage: MutableLiveData<Resource<Message>> = MutableLiveData()

    val updateImage: MutableLiveData<Resource<User>> = MutableLiveData()

    val saveImageResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val saveVideoResponse: MutableLiveData<Resource<User>> = MutableLiveData()

    val saveMultipleImageResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val saveMultipleVideoResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val searchedUsersResponse: MutableLiveData<Resource<SearchedUser>> = MutableLiveData()

    val userSearchedResponse: MutableLiveData<Resource<User>> = MutableLiveData()

    val addSearchedUserResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val deleteSearchedUserResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val sendSingleImageResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val sendSingleVideoResponse: MutableLiveData<Resource<User>> = MutableLiveData()

    val sendMultipleImageResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val sendMultipleVideoResponse: MutableLiveData<Resource<Message>> = MutableLiveData()

    val getUserDetails: MutableLiveData<Resource<User>> = MutableLiveData()

    val getUserSettings: MutableLiveData<Resource<Settings>> = MutableLiveData()

    val updateUserSettings : MutableLiveData<Resource<Settings>> = MutableLiveData()
    fun getNotificationCount(email:String) = viewModelScope.launch {
        handleGetNotificationsCount(email)
    }

    fun getUserNotification(email: String) = viewModelScope.launch {
        handleGetUserNotification(email)
    }

    fun getSavedImages(email: String) = viewModelScope.launch {
        handleGetSavedImage(email)
    }

    fun getSavedVideos(email: String) = viewModelScope.launch {
        handleGetSavedVideos(email)
    }

    fun getSentImages(email: String) = viewModelScope.launch {
        handleGetSentImages(email)
    }

    fun getSentVideos(email: String) = viewModelScope.launch {
        handleGetSentVideos(email)
    }

    fun getReceivedImages(email: String) = viewModelScope.launch {
       handleGetReceivedImages(email)
    }

    fun getReceivedVideos(email: String) = viewModelScope.launch {
        handleGetReceivedVideos(email)
    }

    fun getFavoriteImages(email:String) = viewModelScope.launch {
        handleGetFavoriteImages(email)
    }

    fun getFavoriteVideos(email:String) = viewModelScope.launch {
        handleGetFavoriteVideos(email)
    }

    fun getChatItemList(email: String) = viewModelScope.launch {
        getChatItemsList(email)
    }

    fun getChatMessages(email1: String, email2: String) = viewModelScope.launch {
        getChatMessageList(email1,email2)
    }

    fun deleteSavedImage(email: String, id: String) = viewModelScope.launch {
        performDeleteSavedImage(email,id)
    }

    fun deleteSavedVideo(email: String, id: String) = viewModelScope.launch {
        performDeleteSavedVideo(email, id)
    }

    fun deleteSentImage(fromEmail: String, toEmail: String, id: String) = viewModelScope.launch {
        performDeleteSentImage(fromEmail, toEmail, id)
    }

    fun deleteSentVideo(fromEmail: String, toEmail: String , id: String) = viewModelScope.launch {
        performDeleteSentVideo(fromEmail, toEmail, id)
    }

    fun deleteReceivedImage(fromEmail: String, toEmail: String, id: String) = viewModelScope.launch {
        performDeleteReceivedImage(fromEmail, toEmail, id)
    }

    fun deleteReceivedVideo(fromEmail: String, toEmail: String , id: String) = viewModelScope.launch {
        performDeleteReceivedVideo(fromEmail, toEmail, id)
    }

    fun deleteMultipleSentImage(request: SentImageDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleSentImages(request)
      }

    fun deleteMultipleSentVideo(request: SentVideoDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleSentVideos(request)
    }

    fun deleteMultipleSavedImage(request: ImageDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleSavedImages(request)
    }

    fun deleteMultipleSavedVideo(request: VideoDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleSavedVideos(request)
    }

    fun deleteMultipleFavoriteImage(request: FavoriteImageDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleFavoriteImages(request)
    }

    fun deleteMultipleFavoriteVideo(request: FavoriteVideoDeleteRequest) = viewModelScope.launch {
        performDeleteMultipleFavoriteVideos(request)
    }

    fun addMultipleFavoriteImage(request: FavoriteImageAddRequest) = viewModelScope.launch {
        performAddMultipleFavoriteImages(request)
    }

    fun addMultipleFavoriteVideo(request: FavoriteVideoAddRequest) = viewModelScope.launch {
        performAddMultipleFavoriteVideos(request)
    }
    fun addFavoriteImage(email: String, imageUrl: String) = viewModelScope.launch {
        performAddFavoriteImage(email, imageUrl)
    }

    fun addFavoriteVideo(email: String, videoUrl: String) = viewModelScope.launch {
        performAddFavoriteVideo(email, videoUrl)
    }

    fun deleteFavoriteImage(email: String, id: String) = viewModelScope.launch {
        performDeleteFavoriteImage(email, id)
    }

    fun deleteFavoriteVideo(email: String, id: String) = viewModelScope.launch {
        performDeleteFavoriteVideo(email, id)
    }

    fun updateImage(email: String,imageUri: Uri,context: Context) = viewModelScope.launch {
        performUploadImage(email,imageUri, context)
    }

    fun saveSingleImage(email: String,imageUri: Uri,caption:String,context: Context) = viewModelScope.launch {
        performSaveSingleImage(email, caption, imageUri, context)
    }

    fun saveSingleVideo(email: String,videoUri: Uri,context: Context,caption:String) = viewModelScope.launch {
        performSaveSingleVideo(email, caption, videoUri, context)
    }

    fun saveMultipleImage(email: String,imageUris: List<Uri>,context: Context,caption:String) = viewModelScope.launch {
        performSaveMultipleImage(email,caption, imageUris, context)
    }

    fun saveMultipleVideo(email: String,videosUris: List<Uri>,context: Context,caption:String) = viewModelScope.launch {
        performSaveMultipleVideo(email,caption,videosUris,context)
    }


    fun getSearchedUser(email: String) = viewModelScope.launch {
        performGetUserSearched(email)
    }

    fun addSearchedUser(email: String,searchedEmail: String) = viewModelScope.launch {
         performAddUserSearched(email, searchedEmail)
    }

    fun getAllSearchedUser(email: String) = viewModelScope.launch {
        performGetAllSearchedUser(email)
    }

    fun deleteSearchedUser(id: String) = viewModelScope.launch {
        performDeleteUserSearched(id)
    }

    fun sendSingleImage(fromEmail: String,toEmail: String,imageUri: Uri,caption:String,context: Context) = viewModelScope.launch {
         performSendSingleImage(fromEmail, toEmail, caption, imageUri, context)
    }

    fun sendSingleVideo(fromEmail: String,toEmail: String,videoUri: Uri,context: Context,caption:String) = viewModelScope.launch {
        performSendSingleVideo(fromEmail, toEmail, caption, videoUri, context)
    }

    fun sendMultipleImage(request: ImageSendRequest) = viewModelScope.launch {
        performSendMultipleImage(request)
    }

    fun sendMultipleVideo(request: VideoSendRequest) = viewModelScope.launch {
      performSendMultipleVideo(request)
    }

    fun getUserSettings(email: String) = viewModelScope.launch {
        performGetUserSettings(email)
    }

    fun updateUserSettings(
        email: String,
        darkMode: Boolean,
        language: String,
        notificationOn: Boolean,
        sendNewsLetter: Boolean
    ) = viewModelScope.launch {
        performUpdateUserSettings(email, darkMode, language, notificationOn, sendNewsLetter)
    }
    suspend fun getUserDetails(email: String): User? {
        return performGetUserDetails(email)
    }


    private suspend fun handleGetUserNotification(email: String){
        notifications.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getUserNotification(email)
                if (response.isSuccessful){
                    response.body()?.let { notification ->
                        notificationResponse = notification
                        notifications.postValue(Resource.Success(notificationResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    notifications.postValue(Resource.Error(errorMessage))
                }
            } else{
                notifications.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            notifications.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetNotificationsCount(email: String){
        notificationCount.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getUserNotificationCount(email)
                if (response.isSuccessful){
                    response.body()?.let { notification ->
                        notificationCountResponse = notification
                        notificationCount.postValue(Resource.Success(notificationCountResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    notificationCount.postValue(Resource.Error(errorMessage))
                }
            } else{
                notificationCount.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            notificationCount.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetSavedImage(email: String){
        savedImages.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getSavedImages(email)
                if (response.isSuccessful){
                    response.body()?.let { savedImage->
                        savedImagesResponse = savedImage
                        savedImages.postValue(Resource.Success(savedImagesResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    savedImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                savedImages.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            savedImages.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetSavedVideos(email: String){
        savedVideos.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getSavedVideos(email)
                if (response.isSuccessful){
                    response.body()?.let { savedVideo->
                        savedVideosResponse = savedVideo
                        savedVideos.postValue(Resource.Success(savedVideosResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    savedVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                savedVideos.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            savedVideos.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetSentImages(email: String){
        sentImages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getAllSentImages(email)
                if (response.isSuccessful){
                    response.body()?.let { sentImage->
                        sentImagesResponse = sentImage
                        sentImages.postValue(Resource.Success(sentImagesResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sentImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                sentImages.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            sentImages.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleGetSentVideos(email: String){
        sentVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getAllSentVideo(email)
                if (response.isSuccessful){
                    response.body()?.let { sentVideo->
                        sentVideosResponse = sentVideo
                        sentVideos.postValue(Resource.Success(sentVideosResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sentVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                sentVideos.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            sentVideos.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun handleGetReceivedImages(email: String){
        receivedImages.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getAllReceievedImage(email)
                if (response.isSuccessful){
                    response.body()?.let { receivedImage->
                        receivedImagesResponse = receivedImage
                        receivedImages.postValue(Resource.Success(receivedImagesResponse))
                    }
                    userRepository.updateDeliveryState(email)
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    receivedImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                receivedImages.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            receivedImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun handleGetReceivedVideos(email: String){
        receivedVideos.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getAllReceievedVideos(email)
                if (response.isSuccessful){
                    response.body()?.let { receivedVideo->
                        receivedVideosResponse = receivedVideo
                        receivedVideos.postValue(Resource.Success(receivedVideosResponse))
                    }
                    userRepository.updateDeliveryState(email)
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    receivedVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                receivedVideos.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            receivedVideos.postValue(Resource.Error(e.message.toString()))
        }
    }



    private suspend fun handleGetFavoriteImages(email: String){
        favoriteImages.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getAllFavoriteImages(email)
                if (response.isSuccessful){
                    response.body()?.let { favoriteImage->
                        favoriteImagesResponse = favoriteImage
                        favoriteImages.postValue(Resource.Success(favoriteImagesResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    favoriteImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                favoriteImages.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            favoriteImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun handleGetFavoriteVideos(email: String){
        favoriteVideos.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()){
                val response = userRepository.getAllFavoriteVideos(email)
                if (response.isSuccessful){
                    response.body()?.let { favoriteVideo->
                        favoriteVideosResponse = favoriteVideo
                        favoriteVideos.postValue(Resource.Success(favoriteVideosResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    favoriteVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                favoriteVideos.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            favoriteVideos.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun getChatItemsList(email: String){
        chatItemList.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()){
                val response = userRepository.getChatItemList(email)
                if (response.isSuccessful){
                    response.body()?.let {
                        chatItemList.postValue(Resource.Success(it))
                    }
                    userRepository.updateDeliveryState(email)
                }  else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    chatItemList.postValue(Resource.Error(errorMessage))
                }
            } else{
                chatItemList.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (e:Exception){
            chatItemList.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun getChatMessageList(email1: String,email2: String){
        chatMessageList.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()){
                val response = userRepository.getChatMessageList(email1, email2)
                if (response.isSuccessful){
                    response.body()?.let {
                        chatMessageList.postValue(Resource.Success(it))
                    }
                    userRepository.updateDeliveryState(email1)
                }  else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    chatMessageList.postValue(Resource.Error(errorMessage))
                }
            } else{
                chatMessageList.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (e:Exception){
            chatMessageList.postValue(Resource.Error(e.message.toString()))
        }
    }




    suspend fun showFab(email:String) {

        val show = userRepository.getSavedImages(email).body()?.images!!.isEmpty()
                && userRepository.getSavedVideos(email).body()?.videos!!.isEmpty()
                && userRepository.getAllSentImages(email).body()?.images!!.isEmpty()
                && userRepository.getAllSentVideo(email).body()?.videos!!.isEmpty()
                && userRepository.getAllReceievedImage(email).body()?.images!!.isEmpty()
                && userRepository.getAllReceievedVideos(email).body()?.videos!!.isEmpty()
                && userRepository.getAllFavoriteImages(email).body()?.images!!.isEmpty()
                && userRepository.getAllFavoriteVideos(email).body()?.videos!!.isEmpty()

        showFab.postValue(show)
    }


    private suspend fun performDeleteSavedImage(email: String, id: String){
        deleteSavedImage.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteSavedImage(email, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteSavedImage.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteSavedImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteSavedImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteSavedImage.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performDeleteSavedVideo(email: String, id: String){
        deleteSavedVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteSavedVideo(email, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteSavedVideo.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteSavedVideo.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteSavedVideo.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteSavedVideo.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteSentImage(fromEmail: String, toEmail: String, id: String){
        deleteSentImage.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteSentImage(fromEmail, toEmail, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteSentImage.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteSentImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteSentImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteSentImage.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteSentVideo(fromEmail: String, toEmail: String, id: String){
        deleteSentVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteSentVideo(fromEmail, toEmail, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteSentVideo.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteSentVideo.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteSentVideo.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteSentVideo.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performDeleteReceivedImage(fromEmail: String, toEmail: String, id: String){
        deleteRecievedImage.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteRecievedImage(fromEmail, toEmail, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteRecievedImage.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteRecievedImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteRecievedImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteRecievedImage.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteReceivedVideo(fromEmail: String, toEmail: String, id: String){
        deleteRecievedVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteRecievedVideo(fromEmail, toEmail, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteRecievedVideo.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteRecievedVideo.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteRecievedVideo.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteRecievedVideo.postValue(Resource.Error(e.message.toString()))
        }
    }



    private suspend fun performDeleteFavoriteImage(email: String, id: String){
        deleteFavoriteImage.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteFavoriteImage(email, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteFavoriteImage.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteFavoriteImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteFavoriteImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteFavoriteImage.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteFavoriteVideo(email: String, id: String){
        deleteFavoriteVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteFavoriteVideo(email, id)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteFavoriteVideo.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteFavoriteVideo.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteFavoriteVideo.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteFavoriteVideo.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteMultipleSavedImages(request: ImageDeleteRequest){
        deleteMultipleSavedImages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleSavedImage(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleSavedImages.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleSavedImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleSavedImages.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleSavedImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteMultipleSavedVideos(request: VideoDeleteRequest){
        deleteMultipleSavedVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleSavedVideo(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleSavedVideos.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleSavedVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleSavedVideos.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleSavedVideos.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performDeleteMultipleSentImages(request: SentImageDeleteRequest){
        deleteMultipleSentImages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleImages(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleSentImages.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleSentImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleSentImages.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleSentImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteMultipleSentVideos(request: SentVideoDeleteRequest){
        deleteMultipleSentVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleVideos(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleSentVideos.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleSentVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleSentVideos.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleSentVideos.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteMultipleFavoriteImages(request: FavoriteImageDeleteRequest){
      deleteMultipleFavoriteImages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleFavoriteImage(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleFavoriteImages.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleFavoriteImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleFavoriteImages.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleFavoriteImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteMultipleFavoriteVideos(request: FavoriteVideoDeleteRequest){
        deleteMultipleFavoriteVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteMultipleFavoriteVideo(request )
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        deleteMultipleFavoriteVideos.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteMultipleFavoriteVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteMultipleFavoriteVideos.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteMultipleFavoriteVideos.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performAddMultipleFavoriteImages(request: FavoriteImageAddRequest){
        addMultipleFavoriteImages.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.addMultipleFavoriteImage(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        addMultipleFavoriteImages.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    addMultipleFavoriteImages.postValue(Resource.Error(errorMessage))
                }
            } else{
                addMultipleFavoriteImages.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            addMultipleFavoriteImages.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performAddMultipleFavoriteVideos(request: FavoriteVideoAddRequest){
        addMultipleFavoriteVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.addMultipleFavoriteVideos(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        addMultipleFavoriteVideos.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    addMultipleFavoriteVideos.postValue(Resource.Error(errorMessage))
                }
            } else{
                addMultipleFavoriteVideos.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            addMultipleFavoriteVideos.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performAddFavoriteImage(email: String, imageUrl: String){
        addFavoriteImage.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.addFavoriteImage(email, imageUrl)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        addFavoriteImage.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    addFavoriteImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                addFavoriteImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            addFavoriteImage.postValue(Resource.Error(e.message.toString()))
        }
    }



    private suspend fun performAddFavoriteVideo(email: String, videoUrl: String){
        addFavoriteVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.addFavoriteVideo(email, videoUrl)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        addFavoriteVideo.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    addFavoriteVideo.postValue(Resource.Error(errorMessage))
                }
            } else{
                addFavoriteVideo.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            addFavoriteVideo.postValue(Resource.Error(e.message.toString()))
        }
    }

    @SuppressLint("Recycle")
    private suspend fun performUploadImage(email: String, imageUri: Uri, context: Context){
        updateImage.postValue(Resource.Loading())
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: throw error("Failed to read image")
        val fileName = "image/*"
        val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image",fileName,requestBody)

        try {
            if (hasInternetConnection()){
                val apiUrl = "user/update-image/$email"
                val response = userRepository.updateImage(apiUrl,part)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        updateImage.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    updateImage.postValue(Resource.Error(errorMessage))
                }
            } else{
                updateImage.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            updateImage.postValue(Resource.Error(e.message.toString()))
        }
    }


    @SuppressLint("Recycle")
    private suspend fun performSaveSingleImage(email: String, caption: String, imageUri: Uri, context: Context){
        saveImageResponse.postValue(Resource.Loading())
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: throw error("Failed to read image")
        val fileName = "image/*"
        val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image",fileName,requestBody)

        try {
            if (hasInternetConnection()){
                val response = userRepository.saveImage(email,caption,part)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        saveImageResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    saveImageResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                saveImageResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            saveImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    @SuppressLint("Recycle")
    private suspend fun performSaveSingleVideo(email: String, caption: String, videoUri: Uri, context: Context){
        saveVideoResponse.postValue(Resource.Loading())
        val inputStream = context.contentResolver.openInputStream(videoUri) ?: throw error("Failed to read image")
        val fileName = "video/*"
        val requestBody = inputStream.readBytes().toRequestBody("video/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("video",fileName,requestBody)

        try {
            if (hasInternetConnection()){
                val response = userRepository.saveVideo(email,caption,part)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        saveVideoResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    saveVideoResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                saveVideoResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            saveVideoResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    @SuppressLint("Recycle")
    private suspend fun performSaveMultipleImage(email: String, caption: String, imageUris: List<Uri>, context: Context){
        saveMultipleImageResponse.postValue(Resource.Loading())

        val imageParts = imageUris.map { imgUri ->
            val inputStream = context.contentResolver.openInputStream(imgUri) ?: throw error("Failed to read image")
            val fileName = "image/*"
            val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images",fileName,requestBody)
        }


        try {
            if (hasInternetConnection()){
                val response = userRepository.saveMultipleImages(email,caption,imageParts)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        saveMultipleImageResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    saveMultipleImageResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                saveMultipleImageResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            saveMultipleImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    @SuppressLint("Recycle")
    private suspend fun performSaveMultipleVideo(email: String, caption: String, videoUris: List<Uri>, context: Context){
        saveMultipleVideoResponse.postValue(Resource.Loading())

        val videoParts = videoUris.map { vidUri ->
            val inputStream = context.contentResolver.openInputStream(vidUri) ?: throw error("Failed to read image")
            val fileName = "video/*"
            val requestBody = inputStream.readBytes().toRequestBody("video/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("videos",fileName,requestBody)
        }


        try {
            if (hasInternetConnection()){
                val response = userRepository.saveMultipleVideo(email,caption,videoParts)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        saveMultipleVideoResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    saveMultipleVideoResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                saveMultipleVideoResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            saveMultipleVideoResponse.postValue(Resource.Error(e.message.toString()))
        }
    }




    private suspend fun performGetUserSearched(email: String){
        userSearchedResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getUserEmail(email)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        userSearchedResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    userSearchedResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                userSearchedResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            userSearchedResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performAddUserSearched(email: String, searchedEmail: String){
        addSearchedUserResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.addSearchedUser(email, searchedEmail)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        addSearchedUserResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    addSearchedUserResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                addSearchedUserResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            addSearchedUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performGetAllSearchedUser(email: String){
        searchedUsersResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getAllSearchedEmail(email)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        searchedUsersResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    searchedUsersResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                searchedUsersResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            searchedUsersResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun performDeleteUserSearched(id: String){
        deleteSearchedUserResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.deleteSearchedEmail(id)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        deleteSearchedUserResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    deleteSearchedUserResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                deleteSearchedUserResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            deleteSearchedUserResponse.postValue(Resource.Error(e.message.toString()))
        }
    }





    @SuppressLint("Recycle")
    private suspend fun performSendSingleImage(fromEmail: String, toEmail: String, caption: String, imageUri: Uri, context: Context){
        sendSingleImageResponse.postValue(Resource.Loading())
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: throw error("Failed to read image")
        val fileName = "image/*"
        val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image",fileName,requestBody)

        try {
            if (hasInternetConnection()){
                val response = userRepository.sendImage(fromEmail,toEmail,caption,part)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        sendSingleImageResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sendSingleImageResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                sendSingleImageResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            sendSingleImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    @SuppressLint("Recycle")
    private suspend fun performSendSingleVideo(fromEmail: String, toEmail: String, caption: String, videoUri: Uri, context: Context){
        sendSingleVideoResponse.postValue(Resource.Loading())
        val inputStream = context.contentResolver.openInputStream(videoUri) ?: throw error("Failed to read image")
        val fileName = "video/*"
        val requestBody = inputStream.readBytes().toRequestBody("video/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("video",fileName,requestBody)

        try {
            if (hasInternetConnection()){
                val response = userRepository.sendVideo(fromEmail,toEmail,caption,part)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        sendSingleVideoResponse.postValue(Resource.Success(user))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sendSingleVideoResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                sendSingleVideoResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            sendSingleVideoResponse.postValue(Resource.Error(e.message.toString()))
        }
    }


    @SuppressLint("Recycle")
    private suspend fun performSendMultipleImage(request: ImageSendRequest){
        sendMultipleImageResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.sendMultipleImages(request)
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        sendMultipleImageResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sendMultipleImageResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                sendMultipleImageResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            sendMultipleImageResponse.postValue(Resource.Error(e.message.toString()))
        }
    }

    @SuppressLint("Recycle")
    private suspend fun performSendMultipleVideo(request: VideoSendRequest){
        sendMultipleVideoResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.sendMultipleVideo(request )
                if (response.isSuccessful){
                    response.body()?.let { message ->
                        sendMultipleVideoResponse.postValue(Resource.Success(message))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    sendMultipleVideoResponse.postValue(Resource.Error(errorMessage))
                }
            } else{
                sendMultipleVideoResponse.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            sendMultipleVideoResponse.postValue(Resource.Error(e.message.toString()))
        }
    }



    @SuppressLint("Recycle")
    private suspend fun performGetUserSettings(email: String){
        getUserSettings.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.getUserSettings(email)
                if (response.isSuccessful){
                    response.body()?.let { settings ->
                        getUserSettings.postValue(Resource.Success(settings))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    getUserSettings.postValue(Resource.Error(errorMessage))
                }
            } else{
                getUserSettings.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            getUserSettings.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performUpdateUserSettings(
        email: String,
        darkMode: Boolean,
        language: String,
        notificationOn: Boolean,
        sendNewsLetter: Boolean
    ){
        updateUserSettings.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.updateSettings(email, darkMode, language, notificationOn, sendNewsLetter)
                if (response.isSuccessful){
                    response.body()?.let { settings ->
                        updateUserSettings.postValue(Resource.Success(settings))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    updateUserSettings.postValue(Resource.Error(errorMessage))
                }
            } else{
                updateUserSettings.postValue(Resource.Error("no internet connection"))
            }
        } catch (e:Exception){
            updateUserSettings.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun performGetUserDetails(email: String): User? {
        val response = userRepository.getUserDetails(email)
        return if(response.isSuccessful){
            response.body()
        } else null
    }

















    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return false
    }

}


sealed class CombinedItem {
    data class Image(val imageItem: ImageX) : CombinedItem()
    data class Video(val videoItem: VideoX) : CombinedItem()
}

data class Chat(
    val email: String,
    val date: String,
    val imageUrl: String,
    val type: String
)
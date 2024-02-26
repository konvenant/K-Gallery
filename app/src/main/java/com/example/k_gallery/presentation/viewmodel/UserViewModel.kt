package com.example.k_gallery.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.BaseApplication
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.NotificationCount
import com.example.k_gallery.data.dataSources.api.models.NotificationResponse
import com.example.k_gallery.data.dataSources.api.models.SavedImages
import com.example.k_gallery.data.dataSources.api.models.SavedVideos
import com.example.k_gallery.data.dataSources.api.models.SentImages
import com.example.k_gallery.data.dataSources.api.models.SentVideos
import com.example.k_gallery.data.repositories.remoteRepositories.UserRepository
import com.example.k_gallery.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    val showFab: MutableLiveData<Boolean> = MutableLiveData()



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





    private suspend fun handleGetUserNotification(email: String){
        notifications.postValue(Resource.Loading())
        try {
            val url = "/user/notification/$email"
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
            val url = "/user/notificationCount/$email"
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
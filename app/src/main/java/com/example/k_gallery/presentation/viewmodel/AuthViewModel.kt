package com.example.k_gallery.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.BaseApplication
import com.example.k_gallery.data.dataSources.api.models.Message
import com.example.k_gallery.data.dataSources.api.models.NotificationCount
import com.example.k_gallery.data.dataSources.api.models.NotificationResponse
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.repositories.remoteRepositories.UserRepository
import com.example.k_gallery.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    app: Application
) :AndroidViewModel(app){

    val userDetails: MutableLiveData<Resource<User>> = MutableLiveData()
    lateinit var userDetailResponse: User

    var verifyPasswordDetails: MutableLiveData<Resource<User>> = MutableLiveData()
    lateinit var verifyPasswordDetailResponse: User

    val message: MutableLiveData<Resource<Message>> = MutableLiveData()
    lateinit var messageResponse: Message



    fun login(email: String,password: String){
       viewModelScope.launch {
           safeLogin(email, password)
       }
    }

    fun signUp(email: String,password: String){
        viewModelScope.launch {
            safeSignUp(email, password)
        }
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        handleForgotPassword(email)
    }

    fun verifyForgotPasswordToken(email: String,token:String) = viewModelScope.launch {
        handleVerifyForgotPasswordEmail(email,token)
    }

    fun verifyEmail(email: String,token: Int) = viewModelScope.launch {
        handleVerifyEmail(email, token)
    }

    fun sendToken(email: String,){
        viewModelScope.launch {
          handleSendToken(email)
        }
    }

    fun completeRegistration(
        email: String,
        city: String,
        country: String,
        name: String,
        phone: String){
      viewModelScope.launch {
          handleCompleteRegistration(email, city, country, name, phone)
      }
    }




    fun completeForgotPassword(
        email:String,
        password: String
    ) = viewModelScope.launch {
       handleCompleteForgotPassword(email,password)
    }





    private suspend fun safeLogin(email:String,password:String){
        userDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.login("auth/login",email, password)
                userDetails.postValue(handleLogin(response))
            } else{
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (t:Throwable){
            when(t){
                is IOException -> {
                    userDetails.postValue(Resource.Error("Network Failure"))
                }
                else -> {
                    userDetails.postValue(Resource.Error("Conversion error"))
                    Log.e("Conversion", "${t.message}")
                }
            }
        }
    }

    private fun handleLogin(response: Response<User>): Resource<User> {
        if (response.isSuccessful){
            response.body()?.let { user ->
                userDetailResponse  = user
                return Resource.Success(user)
            }
        }

        val errorBody = response.errorBody()?.string()
        val jsonObj = JSONObject(errorBody!!)
        val error = jsonObj.getString("message")
        val errorMessage = "An Error Occurred: $error"
        return Resource.Error(errorMessage)
    }


    private suspend fun safeSignUp(email:String,password:String){
        message.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.signUp("auth/signup",email, password)
                message.postValue(handleSignUp(response))
            } else{
                message.postValue(Resource.Error("No internet connection"))
            }
        } catch (t:Throwable){
            when(t){
                is IOException -> message.postValue(Resource.Error("Network Failure"))
                else -> message.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private fun handleSignUp(response: Response<Message>): Resource<Message> {
        if (response.isSuccessful){
            response.body()?.let { message ->
                messageResponse = message
                return Resource.Success(messageResponse)
            }
        }

        val errorBody = response.errorBody()?.string()
        val jsonObj = JSONObject(errorBody!!)
        val error = jsonObj.getString("message")
        val errorMessage = "An Error Occurred: $error"
        return Resource.Error(errorMessage)
    }


    private suspend fun handleForgotPassword(email: String){
        userDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepository.forgotPassword(email)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        userDetailResponse = user
                        userDetails.postValue(Resource.Success(userDetailResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    userDetails.postValue(Resource.Error(errorMessage))
                }
            } else{
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }



    private suspend fun handleCompleteRegistration(
        email: String,
        city: String,
        country: String,
        name: String,
        phone: String
    ) {
        userDetails.postValue(Resource.Loading())
        try {
            val url = "/user/update"
            if (hasInternetConnection()){
                val response = userRepository.updateUser(url,email, name, phone, city, country)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        userDetailResponse = user
                        userDetails.postValue(Resource.Success(userDetailResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    userDetails.postValue(Resource.Error(errorMessage))
                }
            } else{
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun handleVerifyEmail(
        email: String,
        token: Int
    ){
        userDetails.postValue(Resource.Loading())
        try {
            val url = "/auth/verify"
            if (hasInternetConnection()){
                val response = userRepository.verifyEmail(url,email,token)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        userDetailResponse = user
                        userDetails.postValue(Resource.Success(userDetailResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    userDetails.postValue(Resource.Error(errorMessage))
                }
            } else{
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleVerifyForgotPasswordEmail(
        email: String,
        token: String
    ){
        verifyPasswordDetails.postValue(Resource.Loading())
        try {
            val url = "/auth/verifyPassword"
            if (hasInternetConnection()){
                val response = userRepository.verifyPassword(url,email,token)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        verifyPasswordDetailResponse = user
                        verifyPasswordDetails.postValue(Resource.Success(verifyPasswordDetailResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    verifyPasswordDetails.postValue(Resource.Error(errorMessage))
                }
            } else{
                verifyPasswordDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            verifyPasswordDetails.postValue(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun handleSendToken(email: String){

        userDetails.postValue(Resource.Loading())
        try {
            val url = "/auth/sendToken"
            if (hasInternetConnection()){
                val response = userRepository.sendToken(url, email)
                if (response.isSuccessful){
                    response.body()?.let { messages ->
                        messageResponse = messages
                        message.postValue(Resource.Success(messages))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    message.postValue(Resource.Error(errorMessage))
                }
            } else{
                message.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            message.postValue(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun handleCompleteForgotPassword(email: String,password: String){


        userDetails.postValue(Resource.Loading())
        try {
            val url = "/user/updatePassword"
            if (hasInternetConnection()){
                val response = userRepository.updatePassword(url, email, password)
                if (response.isSuccessful){
                    response.body()?.let { user ->
                        userDetailResponse = user
                        userDetails.postValue(Resource.Success(userDetailResponse))
                    }
                } else{
                    val errorBody = response.errorBody()?.string()
                    val jsonObj = JSONObject(errorBody!!)
                    val error = jsonObj.getString("message")
                    val errorMessage = "An Error Occurred: $error"
                    userDetails.postValue(Resource.Error(errorMessage))
                }
            } else{
                userDetails.postValue(Resource.Error("No internet connection"))
            }
        } catch (e:Exception){
            userDetails.postValue(Resource.Error(e.message.toString()))
        }
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


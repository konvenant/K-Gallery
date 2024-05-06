package com.example.k_gallery.data.dataSources.api

import com.example.k_gallery.data.dataSources.api.models.ChatItems
import com.example.k_gallery.data.dataSources.api.models.ChatMessageList
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteImages
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoAddRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.FavoriteVideos
import com.example.k_gallery.data.dataSources.api.models.ImageDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.ImageSendRequest
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
import com.example.k_gallery.data.dataSources.api.models.UserX
import com.example.k_gallery.data.dataSources.api.models.VideoDeleteRequest
import com.example.k_gallery.data.dataSources.api.models.VideoSendRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface UserApi {
    @FormUrlEncoded
    @POST
    suspend fun signUp(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<Message>

    @FormUrlEncoded
    @POST
    suspend fun login(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<User>

    @FormUrlEncoded
    @PUT
    suspend fun updateUser(
        @Url url: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("city") city: String,
        @Field("country") country: String
    ): Response<User>

    @FormUrlEncoded
    @POST
    suspend fun verifyEmail(
        @Url url: String,
        @Field("email") email: String,
        @Field("token") token: Int
    ): Response<User>

    @FormUrlEncoded
    @POST
    suspend fun verifyPassword(
        @Url url: String,
        @Field("email") email: String,
        @Field("passwordToken") passwordToken: String
    ): Response<User>

    @FormUrlEncoded
    @POST
    suspend fun sendToken(
        @Url url: String,
        @Field("email") email: String
    ): Response<Message>

    @POST("auth/forgotPassword/{email}")
    suspend fun forgotPassword(@Path("email")email: String): Response<User>


    @FormUrlEncoded
    @PUT
    suspend fun updatePassword(
        @Url url: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<User>

    @Multipart
    @PUT
    suspend fun updateImage(
        @Url url: String,
        @Part image: MultipartBody.Part
    ): Response<User>

    @GET("user/notification/{email}")
    suspend fun getUserNotifications(@Path("email")email: String): Response<NotificationResponse>

    @GET("user/notificationCount/{email}")
    suspend fun getUserNotificationCount(@Path("email")email: String): Response<NotificationCount>

    @GET("user/details/{email}")
    suspend fun getUserDetails(@Path("email")email: String): Response<User>


    @FormUrlEncoded
    @POST("user/update-deliveryState")
    suspend fun updateDeliveryState(
        @Field("email") email: String
    ) : Response<Message>

    @Multipart
    @POST("action/save-image/{email}/{caption}")
    suspend fun saveImage(
        @Path("email") email: String,
        @Path("caption") caption:String,
        @Part image: MultipartBody.Part
    ) : Response<Message>

    @Multipart
    @POST("action/save-video/{email}/{caption}")
    suspend fun saveVideo(
        @Path("email") email: String,
        @Path("caption") caption:String,
        @Part video: MultipartBody.Part
    ) : Response<User>

    @Multipart
    @POST("action/save-multipleImage/{email}/{caption}")
    suspend fun saveMultipleImage(
        @Path("email") email: String,
        @Path("caption") caption: String,
        @Part images: List<MultipartBody.Part>
    ) : Response<Message>

    @Multipart
    @POST("action/save-multipleVideo/{email}/{caption}")
    suspend fun saveMultipleVideo(
        @Path("email") email: String,
        @Path("caption") caption: String,
        @Part videos: List<MultipartBody.Part>,
    ) : Response<Message>

    @GET("action/get-savedImages/{email}")
    suspend fun getSavedImages(
        @Path("email")email: String
    ):Response<SavedImages>

    @GET("action/get-savedVideos/{email}")
    suspend fun getSavedVideos(
        @Path("email")email: String
    ):Response<SavedVideos>

    @POST("action/delete-savedImage/{email}/{id}")
    suspend fun deleteSavedImage(
        @Path("email") email: String,
        @Path("id") id:String
    ):Response<Message>

    //json
    @POST("action/delete-multipleSavedImages")
    suspend fun deleteMultipleSavedImages(
    @Body request: ImageDeleteRequest
    ):Response<Message>

    @POST("action/delete-savedVideo/{email}/{id}")
    suspend fun deleteSavedVideo(
        @Path("email") email: String,
        @Path("id") id:String
    ):Response<Message>

    //json
    @POST("/action/delete-multipleSavedVideos")
    suspend fun deleteMultipleSavedVideos(
  @Body request: VideoDeleteRequest
    ) : Response<Message>

    @GET("action/getAll-sentImages/{email}")
    suspend fun getAllSentImages(
        @Path("email")email: String
    ) : Response<SentImages>

    @GET("action/getAll-sentVideos/{email}")
    suspend fun getAllSentVideos(
        @Path("email")email: String
    ) : Response<SentVideos>

    @GET("action/getAll-recievedImages/{email}")
    suspend fun getAllRecievedImages(
        @Path("email")email: String
    ) : Response<SentImages>

    @GET("action/getAll-RecievedVideos/{email}")
    suspend fun getAllRecievedVideos(
        @Path("email")email: String
    ) : Response<SentVideos>

    @GET("action/getAllChatImages/{fromEmail}/{toEmail}")
    suspend fun getAllChatImages(
        @Path("fromEmail")fromEmail: String,
        @Path("toEmail")toEmail: String
    ): Response<SentImages>

    @GET("action/getAllChatVideos/{fromEmail}/{toEmail}")
    suspend fun getAllChatVideos(
        @Path("fromEmail")fromEmail: String,
        @Path("toEmail")toEmail: String
    ) : Response<SentVideos>

    @FormUrlEncoded
    @POST("action/delete-sentImage")
    suspend fun deleteSentImage(
        @Field("fromEmail") fromEmail: String,
        @Field("toEmail") toEmail: String,
        @Field("id") id: String
    ):Response<Message>

    @FormUrlEncoded
    @POST("action/delete-sentVideo")
    suspend fun deleteSentVideo(
        @Field("fromEmail") fromEmail: String,
        @Field("toEmail") toEmail: String,
        @Field("id") id: String
    ): Response<Message>

    @FormUrlEncoded
    @POST("action/delete-recievedImage")
    suspend fun deleteReceievedImage(
        @Field("fromEmail") fromEmail: String,
        @Field("toEmail") toEmail: String,
        @Field("id") id: String
    ): Response<Message>

    @FormUrlEncoded
    @POST("action/delete-recievedVideo")
    suspend fun deleteReceievedVideo(
        @Field("fromEmail") fromEmail: String,
        @Field("toEmail") toEmail: String,
        @Field("id") id: String
    ) : Response<Message>

    @POST("action/delete-multipleImage")
    suspend fun deleteMultipleImage(
    @Body request: SentImageDeleteRequest
    ) : Response<Message>

    @POST("action/delete-multipleVideos")
    suspend fun deleteMultipleVideos(
        @Body request: SentVideoDeleteRequest
    ) : Response<Message>

    @Multipart
    @POST("action/send-image/{fromEmail}/{toEmail}/{caption}")
    suspend fun sendImage(
        @Path("fromEmail") fromEmail: String,
        @Path("toEmail") toEmail: String,
        @Path("caption") caption: String,
        @Part image: MultipartBody.Part
    ): Response<Message>

    @Multipart
    @POST("action/send-video/{fromEmail}/{toEmail}/{caption}")
    suspend fun sendVideo(
        @Path("fromEmail") fromEmail: String,
        @Path("toEmail") toEmail: String,
        @Path("caption") caption: String,
        @Part video: MultipartBody.Part
    ) : Response<User>

    @POST("action/send-multipleImages")
    suspend fun sendMultipleImage(
        @Body request: ImageSendRequest
    ) : Response<Message>

    @POST("action/send-multipleVideos")
    suspend fun sendMultipleVideos(
        @Body request: VideoSendRequest
    ) : Response<Message>

    @FormUrlEncoded
    @POST("action/addFavoriteImage")
    suspend fun addFavoriteImage(
        @Field("email") email: String,
        @Field("imageUrl") imageUrl: String
    ): Response<Message>

    @FormUrlEncoded
    @POST("action/addFavoriteVideo")
    suspend fun addFavoriteVideo(
        @Field("email") email: String,
        @Field("videoUrl") videoUrl: String
    ):Response<Message>

    @GET("action/getAllFavoriteImages/{email}")
    suspend fun getFavoriteImages(
        @Path("email") email: String
    ) : Response<FavoriteImages>

    @GET("action/getAllFavoriteVideos/{email}")
    suspend fun getFavoriteVideos(
        @Path("email") email: String
    ): Response<FavoriteVideos>

    @FormUrlEncoded
    @POST("action/deleteFavoriteImage")
    suspend fun deleteFavoriteImage(
        @Field("email") email: String,
        @Field("id") id: String
    ): Response<Message>

    @FormUrlEncoded
    @POST("action/deleteFavoriteVideo")
    suspend fun deleteFavoriteVideo(
        @Field("email") email: String,
        @Field("id") id: String
    ) : Response<Message>

    @POST("action/deleteMultipleFavoriteImages")
    suspend fun deleteMultipleFavoriteImage(
      @Body request: FavoriteImageDeleteRequest
    ): Response<Message>

    @POST("action/deleteMultipleFavoriteVideos")
    suspend fun deleteMultipleFavoriteVideo(
        @Body request: FavoriteVideoDeleteRequest
    ) : Response<Message>

    @POST("action/addMultipleFavoriteImages")
    suspend fun addMultipleFavoriteImage(
        @Body request:FavoriteImageAddRequest
    ): Response<Message>

    @POST("action/addMultipleFavoriteVideo")
    suspend fun addMultipleFavoriteVideo(
      @Body request: FavoriteVideoAddRequest
    ) : Response<Message>

    @FormUrlEncoded
    @POST("action/getUser")
    suspend fun getUser(
        @Field("email") email: String
    ):Response<User>

    @FormUrlEncoded
    @POST("action/getUserEmail")
    suspend fun getUserEmail(
        @Field("email") email: String
    ):Response<User>

    @FormUrlEncoded
    @POST("action/addSearchedEmail")
    suspend fun addSearchedEmail(
        @Field("email") email: String,
        @Field("searchedEmail") searchedEmail: String
    ) : Response<Message>

    @FormUrlEncoded
    @POST("action/getAllSearchedEmail")
    suspend fun getAllSearchedEmail(
        @Field("email") email: String
    ) : Response<SearchedUser>

    @FormUrlEncoded
    @POST("action/deleteSearchedEmail")
    suspend fun deleteSearchedEmail(
        @Field("id") id: String
    ) : Response<Message>

    @GET("action/getUserSettings/{email}")
    suspend fun getUserSettings(
        @Path("email") email: String
    ): Response<Settings>

    @FormUrlEncoded
    @POST("action/updateSetting")
    suspend fun updateSetting(
        @Field("email") email: String,
        @Field("darkMode") darkMode: Boolean,
        @Field("language") language : String,
        @Field("notificationOn") notificationOn: Boolean,
        @Field("sendNewsLetter") sendNewsLetter: Boolean
    ) : Response<Settings>

    @GET("action/chatList/{email}")
    suspend fun getChatItemList(
        @Path("email") email: String
    ) : Response<ChatItems>

    @GET("action/chatMesaageList/{email1}/{email2}")
    suspend fun getChatMessageList(
        @Path("email1") email1: String,
        @Path("email2") email2: String
    ) : Response<ChatMessageList>
}
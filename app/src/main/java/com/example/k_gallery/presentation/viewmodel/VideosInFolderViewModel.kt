package com.example.k_gallery.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.data.repositories.localRepositories.VideosInFolderRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VideosInFolderViewModel @Inject constructor(
    private val videosInFolderRepository: VideosInFolderRepository
) : ViewModel() {

    val img : MutableLiveData<Resource<List<Video>>> = MutableLiveData()
    val folderMetrics: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair("0B",0))

    val deleteErr = MutableLiveData<String>()
    val multipleDeleteErr = MutableLiveData<String>()
    val favStatus : MutableLiveData<Resource<String>> = MutableLiveData()

    init {

    }
    fun getVideosInFolder(folderId: String){
        img.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val videoList = videosInFolderRepository.getVideosInFolder(folderId)
                img.postValue(Resource.Success(videoList))
                val pair = calculateFolderMetrics(videoList)
                folderMetrics.postValue(pair)
            } catch (e: Exception){
                img.postValue(Resource.Error("${e.message}"))
            }
        }
    }



    fun deleteVideo(
        videoUri: Uri,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ){
        viewModelScope.launch {
            try {
                context.contentResolver.delete(videoUri,null,null)
                Log.e("Delete Test", "Done Deleting $videoUri")
            } catch (e:SecurityException){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    val intentSender = MediaStore.createDeleteRequest(
                        context.contentResolver,
                        listOf(videoUri)
                    ).intentSender

                    intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    Log.e("Dlll", "Called android 11")
                }
            } catch (e:Exception){
                Log.e("DeleteErr","$e,, ${e.stackTrace}")
                deleteErr.postValue("Error deleting Video: $e .. ")
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    fun deleteMultipleVideos(
        videoList: List<Video>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ){
        viewModelScope.launch {

            try {
                try {
                    for (video in videoList) {
                        context.contentResolver.delete(video.videoUri,null,null)
                        Log.e("Delete Test", "Done Deleting $video.uri")
                    }
                    Log.e("Delete Test", "Done Deleting All")
                } catch (e:SecurityException){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        val intentSender = MediaStore.createDeleteRequest(
                            context.contentResolver,
                            videoList.map { it.videoUri }
                        ).intentSender

                        intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        Log.e("Dlll", "Called android 11")
                    }
                }
            } catch (e:Exception){
                Log.e("DeleteErr","$e,, ${e.stackTrace}")
                multipleDeleteErr.postValue("Error deleting Video: $e .. ")
            }

        }
    }

    fun addVideoToFavorites(
        videoUri: Uri,
        context: Context
    ){
        favStatus.postValue(Resource.Loading())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createFavoriteRequest(
                    context.contentResolver,
                    listOf(videoUri),
                    true
                )
                favStatus.postValue(Resource.Success("Video Successfully added"))
                Log.e("FAV-SUCCESS","successful")
            }
        } catch (e:Exception) {
            favStatus.postValue(Resource.Error("Error: ${e.message}"))
            Log.e("FAV-ERR","$e")
        }
    }




    private fun calculateFolderMetrics(videos: List<Video>) : Pair<String,Int> {
        val totalSize = videos.sumOf { it.size }
        val size = calculateImageSize(totalSize)
        val imageCount = videos.size
        return Pair(size,imageCount)
    }
}
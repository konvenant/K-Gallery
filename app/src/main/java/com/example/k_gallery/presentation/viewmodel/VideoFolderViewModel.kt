package com.example.k_gallery.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.dataSources.local.Video
import com.example.k_gallery.data.dataSources.local.VideoFolder
import com.example.k_gallery.data.repositories.VideoFoldersRepository
import com.example.k_gallery.data.repositories.VideosInFolderRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoFolderViewModel @Inject constructor(
    private val videoFoldersRepository: VideoFoldersRepository,
    private val videosInFolderRepository: VideosInFolderRepository
) : ViewModel() {
    val videoFolder: MutableLiveData<Resource<List<VideoFolder>>> = MutableLiveData()

    val dummyList = mutableListOf<VideoFolder>()

    val kay = videoFoldersRepository.getTestTest()

    val deleteErr = MutableLiveData<String>()
    init {
        loadVideoFolder()
    }

    private fun loadVideoFolder() {
        videoFolder.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
               val folderFromRepo =  videoFoldersRepository.getVideoFoldersWithRecentVideo()
                folderFromRepo.forEach { folder ->
                    val singleFolder = videosInFolderRepository.getVideosInFolder(folder.id.toString())
                    val folderSize = calculateFolderMetrics(singleFolder).first
                    val folderCount = singleFolder.size
                    dummyList.add(VideoFolder(folder.id,folder.name,folder.descVideoUri,folderCount,folderSize))
                }
                videoFolder.postValue(Resource.Success(dummyList))
            } catch (e:Exception){
                videoFolder.postValue(Resource.Error("${e.message}"))
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

    private fun calculateFolderMetrics(videos: List<Video>) : Pair<String,Int> {
        val totalSize = videos.sumOf { it.size }
        val size = calculateImageSize(totalSize)
        val videoCount = videos.size
        return Pair(size,videoCount)
    }

}
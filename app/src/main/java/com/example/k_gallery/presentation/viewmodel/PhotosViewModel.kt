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
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.repositories.localRepositories.PhotosRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository
): ViewModel() {

    val folderMetrics: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair("0B",0))
    val photoImages : MutableLiveData<Resource<List<Image>>> = MutableLiveData()

    val deleteErr = MutableLiveData<String>()
    val multipleDeleteErr = MutableLiveData<String>()

    val favStatus : MutableLiveData<Resource<String>> = MutableLiveData()

    init{
        getAllImages()
    }

   fun getAllImages(){
        photoImages.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
               val images =  photosRepository.getAllImages()
                val pair = calculateFolderMetrics(images)
                folderMetrics.postValue(pair)
                photoImages.postValue(Resource.Success(images))
            } catch (e:Exception){
                photoImages.postValue(e.message?.let { Resource.Error(it) })
            }
        }
    }


    fun deleteImg(
        imageUri: Uri,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ){
        viewModelScope.launch {
            try {
                context.contentResolver.delete(imageUri,null,null)
                Log.e("Delete Test", "Done Deleting $imageUri")
            } catch (e:SecurityException){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    val intentSender = MediaStore.createDeleteRequest(
                        context.contentResolver,
                        listOf(imageUri)
                    ).intentSender

                    intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    Log.e("Dlll", "Called android 11")
                }
            } catch (e:Exception){
                Log.e("DeleteErr","$e,, ${e.stackTrace}")
                deleteErr.postValue("Error deleting image: $e .. ")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun deleteMultipleImage(
        imageList: List<Image>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ){
        viewModelScope.launch {

            try {
                try {
                    for (image in imageList) {
                        context.contentResolver.delete(image.uri,null,null)
                        Log.e("Delete Test", "Done Deleting $image.uri")
                    }
                    Log.e("Delete Test", "Done Deleting All")
                } catch (e:SecurityException){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        val intentSender = MediaStore.createDeleteRequest(
                            context.contentResolver,
                            imageList.map { it.uri }
                        ).intentSender
                        intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                        Log.e("Dlll", "Called android 11")

                    }
                }
            } catch (e:Exception){
                Log.e("DeleteErr","$e,, ${e.stackTrace}")
                multipleDeleteErr.postValue("Error deleting image: $e .. ")
            }
        }
    }

    fun addImageToFavorites(
        imageUri: Uri,
        context: Context
    ){
        favStatus.postValue(Resource.Loading())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createFavoriteRequest(
                    context.contentResolver,
                    listOf(imageUri),
                    true
                )
                favStatus.postValue(Resource.Success("Image Successfully added"))
                Log.e("FAV-SUCCESS","successful")
            }
        } catch (e:Exception) {
            favStatus.postValue(Resource.Error("Error: ${e.message}"))
            Log.e("FAV-ERR","$e")
        }
    }

    private fun calculateFolderMetrics(images: List<Image>) : Pair<String,Int> {
        val totalSize = images.sumOf { it.size }
        val size = calculateImageSize(totalSize)
        val imageCount = images.size
        return Pair(size,imageCount)
    }
}
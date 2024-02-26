package com.example.k_gallery.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.repositories.localRepositories.ImageRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
): ViewModel() {


    private val _viewState = MutableLiveData<ImageViewState>()
    val viewState: LiveData<ImageViewState> get() = _viewState

    val img : MutableLiveData<Resource<List<Image>>> = MutableLiveData()

    val folderMetrics: MutableLiveData<Pair<String,Int>> = MutableLiveData(Pair("0B",0))

    val deleteErr = MutableLiveData<String>()

    val multipleDeleteErr = MutableLiveData<String>()
    val favStatus : MutableLiveData<Resource<String>> = MutableLiveData()

    fun fetchFoldersWithRecentImages(folderId: String) {
        _viewState.value = ImageViewState.Loading
        viewModelScope.launch {
            try {
                val imageList = imageRepository.getImagesInFolder(folderId)
                val pair = calculateFolderMetrics(imageList)
                Log.d("METRICS","$pair")
                folderMetrics.postValue(pair)
                _viewState.value = ImageViewState.Success(imageList)
            } catch (e: Exception) {
                _viewState.value = ImageViewState.Error("Failed to load data: ${e.message}")
                Log.d("Error find","$e")
            }
        }
    }

    suspend fun getImages(folderId: String) : List<Image> {
        return imageRepository.getImagesInFolder(folderId)
    }

    fun getImg(folderId: String) {
        img.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val images = getImages(folderId)
                img.postValue(Resource.Success(images))
            } catch (e:Exception){
                img.postValue(Resource.Error("Error: ${e.message}"))
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



sealed class ImageViewState{
    object Loading : ImageViewState()
    data class Success(val Images: List<Image>): ImageViewState()
    data class Error(val message: String) : ImageViewState()
}

//val intentSender = when {
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
//        MediaStore.createDeleteRequest(context.contentResolver, listOf(imageUri)).intentSender
//    }
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
//        val recoverableSecurityException = e as RecoverableSecurityException
//        recoverableSecurityException.userAction.actionIntent.intentSender
//    }
//    else -> null
//}
//intentSender?.let { sender->
//    intentSenderLauncher.launch(
//        IntentSenderRequest.Builder(sender).build()
//    )
//}
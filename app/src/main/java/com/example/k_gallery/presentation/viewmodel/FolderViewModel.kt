package com.example.k_gallery.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.data.dataSources.local.Folder
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.repositories.localRepositories.FolderRepository
import com.example.k_gallery.data.repositories.localRepositories.ImageRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val imageRepository: ImageRepository
    ) : ViewModel(){

    val folderList : MutableLiveData<Resource<List<Folder>>> = MutableLiveData()


    var dummyList = mutableListOf<Folder>()

    
   val folder: String = "Folders"

     fun fetchFoldersWithRecentImages() {
       folderList.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val folderFromRepo = folderRepository.getFolderWithRecentImages()
                folderFromRepo.forEach { folder ->
                    val singleFolder = imageRepository.getImagesInFolder(folder.folderId.toString())
                    val folderSize = calculateFolderMetrics(singleFolder).first.toString()
                    val folderCount = singleFolder.size
                    dummyList.add(Folder(folder.folderId,folder.folderName,folder.recentImagePath,folderCount,folderSize))
                }
                folderList.postValue(Resource.Success(dummyList))
            } catch (e:Exception){
                folderList.postValue(Resource.Error(e.message!!))
            }
        }
    }

    private fun calculateFolderMetrics(images: List<Image>) : Pair<String,Int> {
        val totalSize = images.sumOf { it.size }
        val size = calculateImageSize(totalSize)
        val imageCount = images.size
        return Pair(size,imageCount)
    }
}




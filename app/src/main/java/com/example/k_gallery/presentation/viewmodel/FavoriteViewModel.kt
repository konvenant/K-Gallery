package com.example.k_gallery.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k_gallery.data.dataSources.local.Image
import com.example.k_gallery.data.repositories.localRepositories.FavoriteRepository
import com.example.k_gallery.presentation.util.Resource
import com.example.k_gallery.presentation.util.calculateImageSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    val folderMetrics: MutableLiveData<Pair<String, Int>> = MutableLiveData(Pair("0B", 0))
    val favoriteImg: MutableLiveData<Resource<List<Image>>> = MutableLiveData()

    init {
        getFavoriteImage()
    }

    fun getFavoriteImage() {
        viewModelScope.launch {
        favoriteImg.postValue(Resource.Loading())
            try {
                val favoriteImage = favoriteRepository.getFavoriteImage()
                val pair = calculateFolderMetrics(favoriteImage)
                favoriteImg.postValue(Resource.Success(favoriteImage))
                folderMetrics.postValue(pair)

            } catch(e:Exception){
                Log.e("FAVORITE","$e")
                favoriteImg.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun calculateFolderMetrics(videos: List<Image>): Pair<String, Int> {
        val totalSize = videos.sumOf { it.size }
        val size = calculateImageSize(totalSize)
        val imageCount = videos.size
        return Pair(size, imageCount)
    }
}
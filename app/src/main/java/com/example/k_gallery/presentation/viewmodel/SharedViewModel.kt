package com.example.k_gallery.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.k_gallery.data.dataSources.api.models.User
import com.example.k_gallery.data.repositories.localRepositories.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
): ViewModel() {
    val userData: MutableLiveData<User> = MutableLiveData()
}
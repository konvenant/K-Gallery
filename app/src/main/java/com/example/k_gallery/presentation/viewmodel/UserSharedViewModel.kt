package com.example.k_gallery.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.k_gallery.data.dataSources.api.models.Settings
import com.example.k_gallery.data.dataSources.api.models.SettingsX
import com.example.k_gallery.data.dataSources.api.models.User

class UserSharedViewModel : ViewModel(){
    val userData: MutableLiveData<User> = MutableLiveData()
    val userSettings : MutableLiveData<SettingsX> = MutableLiveData()
}
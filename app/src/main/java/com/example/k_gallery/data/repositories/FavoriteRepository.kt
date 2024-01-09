package com.example.k_gallery.data.repositories

import com.example.k_gallery.data.dataSources.local.FavoriteDataSource
import com.example.k_gallery.data.dataSources.local.Image
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val favoriteDataSource: FavoriteDataSource
){
    suspend fun getFavoriteImage(): List<Image> =
        favoriteDataSource.getFavouriteImage()
}
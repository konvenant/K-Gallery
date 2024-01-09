package com.example.k_gallery.presentation.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>( data: T?) : Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data,message)
    class Loading<T> : Resource<T>()
}

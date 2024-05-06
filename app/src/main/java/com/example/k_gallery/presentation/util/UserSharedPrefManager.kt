package com.example.k_gallery.presentation.util

import android.content.Context

class UserSharedPrefManager (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs",Context.MODE_PRIVATE)


    private val isLoggedIn = "isLoggedIn"

    private val email = "email"

    private val password = "password"

    private val isFirstTime = "isFirstTime"

    fun getLoggedInPrefs() : UserPreferences{
        return UserPreferences(
            email = sharedPreferences.getString(email,"") ?: " ",
            password = sharedPreferences.getString(password,"")?: "",
            isLoggedIn = sharedPreferences.getBoolean(isLoggedIn,false)
        )
    }

    fun setLoggedInPrefs(userPreferences: UserPreferences){
        with(sharedPreferences.edit()){
            putString(email,userPreferences.email)
            putString(password,userPreferences.password)
            putBoolean(isLoggedIn,userPreferences.isLoggedIn)
            apply()
        }
    }

    fun getFirstTime() : Boolean{
        return sharedPreferences.getBoolean(isFirstTime,true)
    }

    fun setFirstTime(firstTime:Boolean){
        with(sharedPreferences.edit()){
            putBoolean(isFirstTime,firstTime)
            apply()
        }
    }
}
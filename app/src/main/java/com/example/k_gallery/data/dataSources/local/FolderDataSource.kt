package com.example.k_gallery.data.dataSources.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.k_gallery.presentation.util.calculateImageSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FolderDataSource @Inject constructor(
    private val context: Context
) {
    suspend fun getFolderWithRecentImage(): List<Folder> {

        return withContext(Dispatchers.IO){

            val folderList = mutableListOf<Folder>()

          val projection = arrayOf(
              MediaStore.Images.Media.BUCKET_ID,
              MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
              MediaStore.Images.Media.DATA
          )

          val imageUri =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI


          val sortType = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC"

          val cursor = context.contentResolver.query(
              imageUri,
              projection,
              null,
              null,
              sortType
          )

          cursor?.use {
              val bucketIdColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
              val bucketNameColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
              val imagePathIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

              val folderMap = mutableMapOf<Long,Folder>()


              while (it.moveToNext()) {

                  val folderId = it.getLong(bucketIdColumnIndex)
                  val folderName = it.getString(bucketNameColumnIndex)
                  val imagePath = it.getString(imagePathIndex)




                  if (!folderMap.containsKey(folderId)) {
                      val folder = Folder(folderId.toInt(), folderName,imagePath)
                      folderMap[folderId] = folder
                  }
              }
              folderList.addAll(folderMap.values)
              Log.e("ASSxx", "$folderList")
          }


          cursor?.close()
          return@withContext folderList
      }

    }
//
//    private fun getImageUri(imageId: Long): Uri {
//        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        return ContentUris.withAppendedId(contentUri,imageId)
//    }
}
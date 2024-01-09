package com.example.k_gallery.data.dataSources.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosSource @Inject constructor(
    private val context: Context
)  {

    suspend fun getAllImages() : List<Image>{
        return withContext(Dispatchers.IO){
            val images = mutableListOf<Image>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                }  else{
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }


            val contentResolver: ContentResolver = context.contentResolver
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
            )


            val sortType = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            val cursor: Cursor? = contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortType
            )

            cursor?.use {
                val idColumnIndex = it.getColumnIndex(MediaStore.Images.Media._ID)
                val nameColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val pathColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                val dateTakenColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (it.moveToNext()){
                    val imageId = it.getLong(idColumnIndex)
                    val imageName = it.getString(nameColumnIndex)
                    val imagePath = it.getString(pathColumnIndex)
                    val dateTaken= it.getLong(dateTakenColumnIndex)
                    val size = it.getLong(sizeColumnIndex)

                    val imageUri = getImageUri(imageId)
                    val image = Image(imageId,imageName,imagePath,dateTaken,imageUri,size)
                    images.add(image)
                }
            }

            cursor?.close()
            return@withContext images
        }

    }

    private fun getImageUri(imageId: Long): Uri {
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        return Uri.withAppendedPath(contentUri,imageId.toString())
    }
}
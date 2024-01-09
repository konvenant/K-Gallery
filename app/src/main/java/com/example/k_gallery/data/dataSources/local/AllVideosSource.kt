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

class AllVideosSource @Inject constructor(
    private val context: Context
)  {

    suspend fun getAllVideos() : List<Video>{
        return withContext(Dispatchers.IO){
            val videos = mutableListOf<Video>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                }  else{
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }


            val contentResolver: ContentResolver = context.contentResolver
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
            )


            val sortType = "${MediaStore.Video.Media.DATE_ADDED} DESC"
            val cursor: Cursor? = contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortType
            )

            cursor?.use {
                val idColumnIndex = it.getColumnIndex(MediaStore.Video.Media._ID)
                val nameColumnIndex = it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val pathColumnIndex = it.getColumnIndex(MediaStore.Video.Media.DATA)
                val dateTakenColumnIndex = it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val sizeColumnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (it.moveToNext()){
                    val id = it.getLong(idColumnIndex)
                    val name = it.getString(nameColumnIndex)
                    val path = it.getString(pathColumnIndex)
                    val dateTaken= it.getLong(dateTakenColumnIndex)
                    val size = it.getLong(sizeColumnIndex)

                    val videoUri = getVideoUri(id)
                  val video = Video(id,name,path,dateTaken, size,videoUri)
                    videos.add(video)

                }
            }

            cursor?.close()
            return@withContext videos
        }

    }
    private fun getVideoUri(videoId: Long): Uri {
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        return Uri.withAppendedPath(contentUri,videoId.toString())
    }
}
package com.example.k_gallery.data.dataSources.local

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideosInFolderDataSource @Inject constructor(
    private val context: Context
){
    suspend fun getVideosInFolder(folderId: String) : List<Video>{
        return withContext(Dispatchers.IO){
            val videoList = mutableListOf<Video>()

            val collection =
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else{
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE
            )

            val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
            val selectionArgs = arrayOf(folderId)
            val sortBy = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            val cursor = context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortBy
            )

            cursor?.use { c ->
                val idColumnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val uriColumnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val dateTakenColumnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val sizeColumnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (c.moveToNext()) {
                    val videoId =  c.getLong(idColumnIndex)
                    val videoName = c.getString(nameColumnIndex)
                    val videoUri = c.getString(uriColumnIndex)
                    val dateTaken= c.getLong(dateTakenColumnIndex)
                    val size = c.getLong(sizeColumnIndex)

                    val uri = getVideoUri(videoId)
                    val video = Video(videoId,videoName,videoUri,dateTaken, size,uri)
                    videoList.add(video)
                    Log.e("VIDEOES NAME", video.name)
                }
                Log.e("VIDEOESINFOLDER", "$videoList")
            }
            cursor?.close()
            return@withContext videoList

        }
    }
    private fun getVideoUri(videoId: Long): Uri {
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        return Uri.withAppendedPath(contentUri,videoId.toString())
    }
}
package com.example.k_gallery.data.dataSources.local

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoFolderDataSource @Inject constructor(
   private val context: Context
) {
    suspend fun getVideoFolderWithRecentVideo(): List<VideoFolder>{

        return withContext(Dispatchers.IO){

            val videoFolderList = mutableListOf<VideoFolder>()

            val videoUri =
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else{
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA
            )

            val groupBy = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} ASC"

            val cursor = context.contentResolver.query(
                videoUri,
                projection,
                null,
                null,
                groupBy
            )

            cursor?.use { c ->
                val folderIdColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
                val folderNameColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val videoUriColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                val folderMap = mutableMapOf<Long,VideoFolder>()


                while (c.moveToNext()) {
                    val folderId = c.getLong(folderIdColumn)
                    val folderName = c.getString(folderNameColumn)
                    val videoPath = c.getString(videoUriColumn)


                    if (!folderMap.containsKey(folderId)) {
                        val videoFolder = VideoFolder(folderId, folderName,videoPath)
                        folderMap[folderId] = videoFolder
                    }
                }
                videoFolderList.addAll(folderMap.values)
                Log.e("VIDEOS", "videos folder: $videoFolderList")
            }

            cursor?.close()
            return@withContext videoFolderList
        }

    }


    fun getTestTest() : String{
        return "K_Gallery"
    }
}
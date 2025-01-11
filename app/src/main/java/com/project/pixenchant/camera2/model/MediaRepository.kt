package com.project.pixenchant.camera2.model

import android.net.Uri
import android.provider.MediaStore
import com.project.pixenchant.camera2.data.MediaItemData
import com.project.pixenchant.camera2.data.MediaType
import com.project.pixenchant.camera2.data.MediaType.*
import com.project.pixenchant.ext.getAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepository @Inject constructor() {

    /**
     * 当前使用的模式
     */
    private val _showMediaType = MutableStateFlow(CLOSE)
    val showMediaType = _showMediaType

    private val _mediaList = MutableStateFlow(mutableListOf<MediaItemData>())
    val mediaList = _mediaList

    private val _videoList = MutableStateFlow(mutableListOf<MediaItemData>())
    val videoList = _videoList

    private val _imageList = MutableStateFlow(mutableListOf<MediaItemData>())
    val imageList = _imageList

    suspend fun updateMediaType(type: MediaType) {
        _showMediaType.value = type
        if (type == SYNC) {
            fetchAllMedia()
        }
    }

    //根据 Uri 获取媒体文件
    private suspend fun fetchMedia(contentUri: Uri, mediaType: MediaType): List<MediaItemData> = withContext(Dispatchers.IO) {
        val mediaItems = mutableListOf<MediaItemData>()
        val cursor = getAppContext().contentResolver.query(
            contentUri,
            arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATE_ADDED),
            null,
            null,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        )
        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(columnIndex)
                val uri = Uri.withAppendedPath(contentUri, id.toString())
                mediaItems.add(MediaItemData(uri, mediaType))
            }
        }
        mediaItems
    }

    //所有媒体文件
    private suspend fun fetchAllMedia()= withContext(Dispatchers.IO) {

        _mediaList.value.clear()
        _videoList.value.clear()
        _imageList.value.clear()

        val contentUri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC" // 按时间降序排序

        val cursor = getAppContext().contentResolver.query(
            contentUri,
            arrayOf( // 查询 ID 和媒体类型
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE
            ),
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumnIndex)
                val mediaType = getMediaType(cursor.getInt(typeColumnIndex))
                val uri = Uri.withAppendedPath(contentUri, id.toString())
                val mediaItemData = MediaItemData(uri, mediaType)
                _mediaList.value.add(mediaItemData)
                when(mediaType) {
                    IMAGES -> _imageList.value.add(mediaItemData)
                    VIDEOS -> _videoList.value.add(mediaItemData)
                    else -> {}
                }
            }
        }
    }

    private fun getMediaType(type: Int): MediaType {
        return when (type) {
            // 图片
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> IMAGES
            // 视频
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> VIDEOS
            else ->  ALL
        }
    }
}

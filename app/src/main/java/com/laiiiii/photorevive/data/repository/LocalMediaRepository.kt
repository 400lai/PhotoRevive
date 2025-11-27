package com.laiiiii.photorevive.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.laiiiii.photorevive.ui.recyclerview.model.MediaItem
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

object LocalMediaRepository {

    suspend fun loadImagesFromDevice(context: Context): List<MediaItem> = withContext(Dispatchers.IO) {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MIME_TYPE
        )
        // 按拍摄时间倒序，再按 ID 倒序（避免 DATE_TAKEN=0 的图片乱序）
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC, ${MediaStore.Images.Media._ID} DESC"

        val items = mutableListOf<MediaItem>()
        context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mimeType = cursor.getString(mimeColumn)
                if (mimeType.startsWith("image/")) {
                    val uri = Uri.withAppendedPath(collection, id.toString()).toString()
                    items.add(MediaItem(id = id, uri = uri, isVideo = false))
                }
            }
        }
        items
    }
}
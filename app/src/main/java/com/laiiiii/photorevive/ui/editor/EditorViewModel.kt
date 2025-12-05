package com.laiiiii.photorevive.ui.editor

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.laiiiii.photorevive.ui.editor.model.TransformState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val _editorState = MutableLiveData<EditorState>()
    val editorState: LiveData<EditorState> = _editorState

    private val _exportState = MutableLiveData<ExportState>()
    val exportState: LiveData<ExportState> = _exportState

    init {
        _editorState.value = EditorState.Ready()
        _exportState.value = ExportState.Idle
    }

    fun updateTransform(transformState: TransformState) {
        val currentState = _editorState.value
        if (currentState is EditorState.Ready) {
            _editorState.value = currentState.copy(transformState = transformState)
        }
    }

    /**
     * 导出图像到相册
     */
    fun exportImage(bitmap: Bitmap, cropRect: android.graphics.RectF) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading

            try {
                // 裁剪子图
                val cropped = Bitmap.createBitmap(
                    bitmap,
                    cropRect.left.toInt().coerceAtLeast(0),
                    cropRect.top.toInt().coerceAtLeast(0),
                    cropRect.width().toInt().coerceAtMost(bitmap.width - cropRect.left.toInt()),
                    cropRect.height().toInt().coerceAtMost(bitmap.height - cropRect.top.toInt())
                )

                val uri = withContext(Dispatchers.IO) {
                    saveImageToGallery(cropped)
                }

                if (uri != null) {
                    _exportState.value = ExportState.Success(uri)
                } else {
                    _exportState.value = ExportState.Error("保存失败")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "裁剪或保存失败")
            }
        }
    }

    /**
     * 将图像保存到相册
     */
    private suspend fun saveImageToGallery(bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val displayName = "photo_revive_${System.currentTimeMillis()}.jpg"

                // 使用更完整的 ContentValues
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { imageUri ->
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = resolver.openOutputStream(imageUri)
                        val success = outputStream?.let { stream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        } ?: false

                        if (success) {
                            // 强制通知媒体扫描
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                // 对于 Android Q 以下版本，使用 MediaScannerConnection
                                android.media.MediaScannerConnection.scanFile(
                                    context,
                                    arrayOf(imageUri.toString()),
                                    arrayOf("image/jpeg")
                                ) { path, uri ->
                                    // 扫描完成回调
                                }
                            } else {
                                // Android Q 及以上版本，插入时就已经触发扫描
                            }

                            imageUri
                        } else {
                            resolver.delete(imageUri, null, null)
                            null
                        }
                    } catch (e: Exception) {
                        resolver.delete(imageUri, null, null)
                        null
                    } finally {
                        outputStream?.close()
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

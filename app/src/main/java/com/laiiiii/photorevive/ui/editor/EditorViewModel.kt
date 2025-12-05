// ui/editor/EditorViewModel.kt
package com.laiiiii.photorevive.ui.editor

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
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
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "photo_revive_${System.currentTimeMillis()}.jpg")
                    put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { imageUri ->
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = resolver.openOutputStream(imageUri)
                        // 修复点：使用安全调用避免 NullPointerException，并判断是否成功压缩
                        val success = outputStream?.let { stream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        } ?: false

                        if (success) {
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

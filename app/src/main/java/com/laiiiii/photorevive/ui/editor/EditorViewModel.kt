// ui/editor/EditorViewModel.kt
package com.laiiiii.photorevive.ui.editor

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import com.laiiiii.photorevive.ui.editor.model.TransformState

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
    fun exportImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading

            try {
                val uri = withContext(Dispatchers.IO) {
                    saveImageToGallery(bitmap)
                }

                if (uri != null) {
                    _exportState.value = ExportState.Success(uri)
                } else {
                    _exportState.value = ExportState.Error("保存失败")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "未知错误")
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

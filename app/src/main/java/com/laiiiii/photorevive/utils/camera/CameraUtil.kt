package com.laiiiii.photorevive.utils.camera

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraUtil(private val fragment: Fragment) {

    private var photoURI: Uri? = null
    private lateinit var photoFile: File

    // 创建临时图片文件（应用私有目录）
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "PhotoRevive_${timeStamp}.jpg"
        val storageDir = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFileName).apply {
            parentFile?.mkdirs()
        }
    }

    // 准备拍照文件
    fun preparePhotoFile(): File {
        photoFile = createImageFile()
        photoURI = FileProvider.getUriForFile(
            fragment.requireContext(),
            "${fragment.requireContext().packageName}.fileprovider",
            photoFile
        )
        return photoFile
    }

    // 启动相机
    fun launchCamera(cameraLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null && photoURI != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(intent)
        }
    }

    // 将图片保存到系统相册（MediaStore）
    fun saveImageToPublicGallery(sourceFile: File, onComplete: (() -> Unit)? = null) {
        val resolver = fragment.requireContext().contentResolver
        val displayName = sourceFile.name
        val relativePath = Environment.DIRECTORY_PICTURES + "/PhotoRevive"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
        }

        try {
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    sourceFile.inputStream().use { input ->
                        input.copyTo(outputStream)
                    }
                }
                onComplete?.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 获取当前照片文件
    fun getCurrentPhotoFile(): File = photoFile
}

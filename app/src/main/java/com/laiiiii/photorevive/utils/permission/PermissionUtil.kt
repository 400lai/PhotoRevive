package com.laiiiii.photorevive.utils.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {
    // 相机权限相关
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(fragment: Fragment, onPermissionResult: (Boolean) -> Unit): ActivityResultLauncher<String> {
        return fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult(isGranted)
        }
    }

    // 相册权限相关
    fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, getRequiredPermission(context)) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(activity: ComponentActivity, onPermissionResult: (Boolean) -> Unit): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult(isGranted)
        }
    }

    // 将 private 改为 internal 并添加 @JvmStatic 注解以便在 Java/Kotlin 中调用
    internal fun getRequiredPermission(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}

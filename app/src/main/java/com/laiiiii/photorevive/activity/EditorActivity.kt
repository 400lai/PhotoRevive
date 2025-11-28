// activity/EditorActivity.kt (保持不变)
package com.laiiiii.photorevive.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.laiiiii.photorevive.databinding.ActivityEditorBinding
import com.laiiiii.photorevive.ui.editor.EditorTouchListener
import com.laiiiii.photorevive.ui.editor.EditorViewModel
import com.laiiiii.photorevive.ui.editor.ExportState
import com.laiiiii.photorevive.ui.editor.ImageRenderer
import java.io.InputStream

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var viewModel: EditorViewModel
    private var currentBitmap: Bitmap? = null
    private var imageRenderer: ImageRenderer? = null
    private lateinit var touchListener: EditorTouchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 ViewModel
        viewModel = ViewModelProvider(this)[EditorViewModel::class.java]

        // 观察导出状态
        viewModel.exportState.observe(this) { state ->
            when (state) {
                is ExportState.Loading -> {
                    // 显示加载状态
                    binding.btnExport.text = "导出中..."
                    binding.btnExport.isEnabled = false
                }
                is ExportState.Success -> {
                    // 导出成功
                    binding.btnExport.text = "导出"
                    binding.btnExport.isEnabled = true
                    Toast.makeText(this, "图片已保存到相册", Toast.LENGTH_SHORT).show()
                }
                is ExportState.Error -> {
                    // 导出失败
                    binding.btnExport.text = "导出"
                    binding.btnExport.isEnabled = true
                    Toast.makeText(this, "导出失败: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                is ExportState.Idle -> {
                    binding.btnExport.text = "导出"
                    binding.btnExport.isEnabled = true
                }
            }
        }

        // 隐藏系统状态栏（移到 setContentView 之后）
        hideSystemUI()

        // 设置关闭按钮的点击监听器
        binding.btnClose.setOnClickListener {
            finish() // 关闭当前活动，返回到相册页
        }

        // 设置导出按钮的点击监听器
        binding.btnExport.setOnClickListener {
            currentBitmap?.let { bitmap ->
                viewModel.exportImage(bitmap)
            } ?: run {
                Toast.makeText(this, "图片尚未加载完成", Toast.LENGTH_SHORT).show()
            }
        }

        // 获取传递过来的图片 URI
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)

            // 从 URI 加载图片
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                currentBitmap = bitmap
                // 设置渲染器
                imageRenderer = ImageRenderer(bitmap)
                binding.glSurfaceView.setRenderer(imageRenderer)
                binding.glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

                // 初始化手势检测器
                initGestureDetectors()
            }
        }
    }

    private fun initGestureDetectors() {
        touchListener = EditorTouchListener { transformState ->
            // 更新渲染器中的变换状态
            imageRenderer?.updateTransform(transformState)
            binding.glSurfaceView.requestRender()
        }

        // 设置触摸监听器
        binding.glSurfaceView.setEditorTouchListener(touchListener)
    }

    /**
     * 根据不同的 Android 版本隐藏系统 UI。
     */
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 使用 WindowInsetsController API（Android 11+）
            window.insetsController?.apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // 使用旧版 API（Android 10 及以下）
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    // 当用户交互时保持隐藏状态
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}

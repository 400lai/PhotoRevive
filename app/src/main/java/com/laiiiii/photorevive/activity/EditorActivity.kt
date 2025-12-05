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
import com.laiiiii.photorevive.ui.editor.CropManager
import com.laiiiii.photorevive.ui.editor.CropState
import com.laiiiii.photorevive.ui.editor.EditorHistoryManager
import com.laiiiii.photorevive.ui.editor.EditorTouchListener
import com.laiiiii.photorevive.ui.editor.EditorViewModel
import com.laiiiii.photorevive.ui.editor.ExportState
import com.laiiiii.photorevive.ui.editor.ImageRenderer
import com.laiiiii.photorevive.ui.editor.model.TransformState

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var viewModel: EditorViewModel
    private var currentBitmap: Bitmap? = null
    private var imageRenderer: ImageRenderer? = null
    private lateinit var touchListener: EditorTouchListener
    private val historyManager = EditorHistoryManager()
    private var currentCropState = CropState(isActive = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[EditorViewModel::class.java]

        viewModel.exportState.observe(this) { state ->
            when (state) {
                is ExportState.Loading -> {
                    binding.btnExport.text = "导出中..."
                    binding.btnExport.isEnabled = false
                }
                is ExportState.Success -> {
                    binding.btnExport.text = "导出"
                    binding.btnExport.isEnabled = true
                    Toast.makeText(this, "图片已保存到相册", Toast.LENGTH_SHORT).show()
                }
                is ExportState.Error -> {
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

        hideSystemUI()

        binding.btnClose.setOnClickListener { finish() }

        binding.btnExport.setOnClickListener {
            currentBitmap?.let { bitmap ->
                // 将 view 坐标下的 cropRect 映射回 bitmap 像素坐标
                val viewRect = currentCropState.cropRect
                val mappedRect = mapViewRectToBitmapRect(
                    viewRect,
                    bitmap.width,
                    bitmap.height,
                    binding.glSurfaceView.width,
                    binding.glSurfaceView.height
                )
                viewModel.exportImage(bitmap, mappedRect)
            } ?: run {
                Toast.makeText(this, "图片尚未加载完成", Toast.LENGTH_SHORT).show()
            }
        }

        // 加载图片
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (!imageUriString.isNullOrEmpty()) {
            val uri = Uri.parse(imageUriString)
            try {
                contentResolver.openInputStream(uri)?.use { stream ->
                    currentBitmap = BitmapFactory.decodeStream(stream)
                    setupRenderer()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "无效的图片路径", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Undo/Redo/Reset
        binding.btnUndo.setOnClickListener {
            if (historyManager.canUndo()) {
                val snapshot = historyManager.undo()
                snapshot?.let {
                    imageRenderer?.updateTransform(it.transform)
                    currentCropState = it.crop
                    imageRenderer?.setCropBox(it.crop.cropRect)
                    binding.glSurfaceView.requestRender()
                }
            }
        }

        binding.btnRedo.setOnClickListener {
            if (historyManager.canRedo()) {
                val snapshot = historyManager.redo()
                snapshot?.let {
                    imageRenderer?.updateTransform(it.transform)
                    currentCropState = it.crop
                    imageRenderer?.setCropBox(it.crop.cropRect)
                    binding.glSurfaceView.requestRender()
                }
            }
        }

        binding.btnReset.setOnClickListener {
            val defaultTransform = TransformState.DEFAULT
            imageRenderer?.updateTransform(defaultTransform)
            currentCropState = currentCropState.copy(
                aspectRatio = null,
                cropRect = CropManager.calculateInitialCropRect(
                    binding.glSurfaceView.width,
                    binding.glSurfaceView.height,
                    currentBitmap?.width ?: 1,
                    currentBitmap?.height ?: 1
                )
            )
            imageRenderer?.setCropBox(currentCropState.cropRect)
            historyManager.saveSnapshot(defaultTransform, currentCropState)
            binding.glSurfaceView.requestRender()
        }

        // 裁剪比例按钮
        binding.btn11.setOnClickListener { setCropAspectRatio(1f) }
        binding.btn34.setOnClickListener { setCropAspectRatio(3f / 4f) }
        binding.btn43.setOnClickListener { setCropAspectRatio(4f / 3f) }
        binding.btn916.setOnClickListener { setCropAspectRatio(9f / 16f) }
        binding.btn169.setOnClickListener { setCropAspectRatio(16f / 9f) }
        binding.btnOriginal.setOnClickListener {
            currentBitmap?.let { bmp ->
                setCropAspectRatio(bmp.width.toFloat() / bmp.height)
            }
        }
        binding.btnFree.setOnClickListener { setCropAspectRatio(null) }
    }

    private fun setupRenderer() {
        currentBitmap?.let { bitmap ->
            val initialCrop = CropManager.calculateInitialCropRect(
                binding.glSurfaceView.width,
                binding.glSurfaceView.height,
                bitmap.width,
                bitmap.height
            )
            currentCropState = CropState(isActive = true, aspectRatio = null, cropRect = initialCrop)

            imageRenderer = ImageRenderer(bitmap)
            imageRenderer?.setCropBox(initialCrop)
            binding.glSurfaceView.setRenderer(imageRenderer)
            binding.glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

            initGestureDetectors()

            historyManager.saveSnapshot(TransformState.DEFAULT, currentCropState)
        }
    }

    private fun initGestureDetectors() {
        touchListener = EditorTouchListener { transformState ->
            imageRenderer?.updateTransform(transformState)
            binding.glSurfaceView.requestRender()
            historyManager.saveSnapshot(transformState, currentCropState)
        }
        binding.glSurfaceView.setEditorTouchListener(touchListener)
    }

    private fun setCropAspectRatio(ratio: Float?) {
        currentBitmap?.let { bitmap ->
            currentCropState = currentCropState.copy(aspectRatio = ratio)
            val newRect = CropManager.applyAspectRatio(
                currentCropState.cropRect,
                ratio,
                binding.glSurfaceView.width,
                binding.glSurfaceView.height
            )
            currentCropState = currentCropState.copy(cropRect = newRect)
            imageRenderer?.setCropBox(newRect)
            val currentTransform = touchListener.getCurrentTransform()
            historyManager.saveSnapshot(currentTransform, currentCropState)
            binding.glSurfaceView.requestRender()
        }
    }

    private fun mapViewRectToBitmapRect(
        viewRect: android.graphics.RectF,
        bitmapW: Int,
        bitmapH: Int,
        viewW: Int,
        viewH: Int
    ): android.graphics.RectF {
        // 先获取图像在 view 中的实际显示区域（居中、缩放）
        val scale = minOf(viewW / bitmapW.toFloat(), viewH / bitmapH.toFloat(), 1.0f)
        val displayedW = bitmapW * scale
        val displayedH = bitmapH * scale
        val offsetX = (viewW - displayedW) / 2f
        val offsetY = (viewH - displayedH) / 2f

        // viewRect 相对于 displayed 区域的偏移
        val relLeft = (viewRect.left - offsetX) / displayedW
        val relTop = (viewRect.top - offsetY) / displayedH
        val relRight = (viewRect.right - offsetX) / displayedW
        val relBottom = (viewRect.bottom - offsetY) / displayedH

        // 映射回 bitmap 像素
        val left = (relLeft * bitmapW).coerceIn(0f, bitmapW.toFloat())
        val top = (relTop * bitmapH).coerceIn(0f, bitmapH.toFloat())
        val right = (relRight * bitmapW).coerceIn(0f, bitmapW.toFloat())
        val bottom = (relBottom * bitmapH).coerceIn(0f, bitmapH.toFloat())

        return android.graphics.RectF(left, top, right, bottom)
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}
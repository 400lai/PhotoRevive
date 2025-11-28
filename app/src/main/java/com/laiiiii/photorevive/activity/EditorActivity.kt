package com.laiiiii.photorevive.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.laiiiii.photorevive.databinding.ActivityEditorBinding
import com.laiiiii.photorevive.ui.editor.ImageRenderer
import java.io.InputStream

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置关闭按钮的点击监听器
        binding.btnClose.setOnClickListener {
            finish() // 关闭当前活动，返回到相册页
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
                // 设置渲染器
                val renderer = ImageRenderer(bitmap)
                binding.glSurfaceView.setRenderer(renderer)
                binding.glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            }
        }
    }
}
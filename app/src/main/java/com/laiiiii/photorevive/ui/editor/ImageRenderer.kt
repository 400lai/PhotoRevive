package com.laiiiii.photorevive.ui.editor

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageRenderer(private val bitmap: Bitmap) : GLSurfaceView.Renderer {
    private var program = 0
    private var textureId = -1
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var displayVertices = FloatArray(8) // 用于存储调整后的顶点坐标

    private val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uTexture;
        varying vec2 vTexCoord;
        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    """.trimIndent()

    // 顶点坐标 (x, y)
    private val vertices = floatArrayOf(
        -1f, 1f,   // 左上
        -1f, -1f,  // 左下
        1f, 1f,    // 右上
        1f, -1f    // 右下
    )

    // 纹理坐标 (s, t)
    private val textureCoords = floatArrayOf(
        0f, 0f,  // 左上
        0f, 1f,  // 左下
        1f, 0f,  // 右上
        1f, 1f   // 右下
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // 初始化默认顶点缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(textureCoords)
                position(0)
            }

        // 创建着色器程序
        program = createProgram(vertexShaderCode, fragmentShaderCode)
        if (program == 0) {
            Log.e("ImageRenderer", "Failed to create program")
            return
        }

        // 加载纹理
        textureId = loadTexture(bitmap)
        if (textureId == -1) {
            Log.e("ImageRenderer", "Failed to load texture")
            return
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // 计算centerInside效果的顶点坐标
        calculateCenterInsideVertices(width, height)

        // 更新顶点缓冲区
        vertexBuffer.clear()
        vertexBuffer.put(displayVertices)
        vertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        if (program == 0) {
            Log.e("ImageRenderer", "Program is not valid")
            return
        }

        GLES20.glUseProgram(program)

        // 设置顶点属性
        val positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        if (positionHandle == -1) {
            Log.e("ImageRenderer", "Failed to get attribute location for aPosition")
            return
        }
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // 设置纹理坐标属性
        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        if (texCoordHandle == -1) {
            Log.e("ImageRenderer", "Failed to get attribute location for aTexCoord")
            return
        }
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        if (textureHandle != -1) {
            GLES20.glUniform1i(textureHandle, 0)
        }

        // 绘制两个三角形形成矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun calculateCenterInsideVertices(viewWidth: Int, viewHeight: Int) {
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        // 如果图片尺寸小于等于视图尺寸，则保持原大小居中显示
        // 如果图片尺寸大于视图尺寸，则按比例缩小以完整显示
        val widthScale = viewWidth.toFloat() / bitmapWidth
        val heightScale = viewHeight.toFloat() / bitmapHeight

        // 选择较小的缩放因子以确保整个图片都能显示在视图内
        val scale = minOf(widthScale, heightScale, 1.0f)

        // 计算实际显示尺寸
        val displayWidth = bitmapWidth * scale
        val displayHeight = bitmapHeight * scale

        // 计算在标准化设备坐标系中的位置(-1 to 1)
        val normalizedWidth = displayWidth / viewWidth
        val normalizedHeight = displayHeight / viewHeight

        // 更新顶点坐标数组以实现centerInside效果
        displayVertices = floatArrayOf(
            -normalizedWidth, normalizedHeight,   // 左上
            -normalizedWidth, -normalizedHeight,  // 左下
            normalizedWidth, normalizedHeight,    // 右上
            normalizedWidth, -normalizedHeight    // 右下
        )
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) {
            Log.e("ImageRenderer", "Failed to create shader")
            return 0
        }

        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // 检查编译状态
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val infoLog = GLES20.glGetShaderInfoLog(shader)
            Log.e("ImageRenderer", "Shader compilation failed: $infoLog")
            Log.e("ImageRenderer", "Shader source:\n$shaderCode")
            GLES20.glDeleteShader(shader)
            return 0
        }

        return shader
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) return 0

        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) return 0

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        // 检查链接状态
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val infoLog = GLES20.glGetProgramInfoLog(program)
            Log.e("ImageRenderer", "Program linking failed: $infoLog")
            GLES20.glDeleteProgram(program)
            return 0
        }

        // 删除着色器对象因为我们已经链接到程序中了
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        return program
    }

    private fun loadTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        val textureId = textures[0]

        if (textureId == 0) {
            Log.e("ImageRenderer", "Failed to generate texture")
            return -1
        }

        // 在设置纹理参数之前先绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        // 设置纹理参数
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // 加载纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        // 注意：这里不应该回收bitmap，因为它会在后续渲染中使用
        // bitmap.recycle()

        return textureId
    }
}

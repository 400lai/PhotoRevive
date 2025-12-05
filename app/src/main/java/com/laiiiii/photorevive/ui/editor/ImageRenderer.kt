package com.laiiiii.photorevive.ui.editor

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.laiiiii.photorevive.ui.editor.model.TransformState
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageRenderer(private val bitmap: Bitmap) : GLSurfaceView.Renderer {
    private var program = 0
    private var cropProgram = 0 // 专门用于绘制裁剪框
    private var textureId = -1
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var cropVertexBuffer: FloatBuffer
    private var displayVertices = FloatArray(8)

    // 裁剪框归一化坐标 (x1, y1, x2, y2 in [-1,1])
    private var cropBoxNormalized = floatArrayOf(-1f, 1f, 1f, -1f) // 默认全屏

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

    // 裁剪框着色器（纯色线框）
    private val cropVertexShader = """
        attribute vec2 aPosition;
        void main() {
            gl_Position = vec4(aPosition, 0.0, 1.0);
        }
    """.trimIndent()

    private val cropFragmentShader = """
        precision mediump float;
        void main() {
            gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0); // 白色
        }
    """.trimIndent()

    private val vertices = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, 1f,
        1f, -1f
    )

    private val textureCoords = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 0f,
        1f, 1f
    )

    private var transformState = TransformState.DEFAULT
    private var viewWidth = 0
    private var viewHeight = 0
    private var initialScale = 1.0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        vertexBuffer = allocateFloatBuffer(vertices)
        textureBuffer = allocateFloatBuffer(textureCoords)

        program = createProgram(vertexShaderCode, fragmentShaderCode)
        cropProgram = createProgram(cropVertexShader, cropFragmentShader)

        textureId = loadTexture(bitmap)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        viewWidth = width
        viewHeight = height

        val bitmapW = bitmap.width.toFloat()
        val bitmapH = bitmap.height.toFloat()
        val wScale = width / bitmapW
        val hScale = height / bitmapH
        initialScale = minOf(wScale, hScale, 1.0f)

        calculateCenterInsideVertices(width, height)
        vertexBuffer.put(displayVertices)
        vertexBuffer.position(0)

        // 初始化裁剪框为全图（归一化）
        updateCropBoxFromRectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        return program
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    // 替换掉原来的 loadTexture(bitmap) 调用
    private fun loadTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        val textureId = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        return textureId
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 绘制图片
        if (program != 0 && textureId != -1) {
            GLES20.glUseProgram(program)
            bindAttributesAndTexture()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }

        // 绘制裁剪框（线框）
        if (cropProgram != 0) {
            GLES20.glUseProgram(cropProgram)
            val posHandle = GLES20.glGetAttribLocation(cropProgram, "aPosition")
            GLES20.glEnableVertexAttribArray(posHandle)
            GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, cropVertexBuffer)

            GLES20.glLineWidth(3.0f)
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4)

            GLES20.glDisableVertexAttribArray(posHandle)
        }
    }

    private fun bindAttributesAndTexture() {
        val pos = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(pos)
        GLES20.glVertexAttribPointer(pos, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val tex = GLES20.glGetAttribLocation(program, "aTexCoord")
        GLES20.glEnableVertexAttribArray(tex)
        GLES20.glVertexAttribPointer(tex, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val uTex = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(uTex, 0)
    }

    fun updateTransform(transformState: TransformState) {
        this.transformState = transformState
        updateVerticesWithTransform()
    }

    fun setCropBox(viewRect: android.graphics.RectF) {
        updateCropBoxFromRectF(viewRect.left, viewRect.top, viewRect.right, viewRect.bottom)
    }

    private fun updateCropBoxFromRectF(left: Float, top: Float, right: Float, bottom: Float) {
        if (viewWidth == 0 || viewHeight == 0) return // 防止除零

        val x1 = (left / viewWidth) * 2 - 1
        val y1 = 1 - (top / viewHeight) * 2
        val x2 = (right / viewWidth) * 2 - 1
        val y2 = 1 - (bottom / viewHeight) * 2

        cropBoxNormalized = floatArrayOf(
            x1, y1,
            x1, y2,
            x2, y2,
            x2, y1
        )
        cropVertexBuffer = allocateFloatBuffer(cropBoxNormalized)
    }

    private fun updateVerticesWithTransform() {
        val bw = bitmap.width.toFloat()
        val bh = bitmap.height.toFloat()
        val sx = initialScale * transformState.scaleX
        val sy = initialScale * transformState.scaleY
        val dw = bw * sx
        val dh = bh * sy
        val nw = dw / viewWidth
        val nh = dh / viewHeight
        val tx = (transformState.translateX * 2f) / viewWidth
        val ty = (transformState.translateY * 2f) / viewHeight

        displayVertices = floatArrayOf(
            -nw + tx, nh - ty,
            -nw + tx, -nh - ty,
            nw + tx, nh - ty,
            nw + tx, -nh - ty
        )
        vertexBuffer.put(displayVertices)
        vertexBuffer.position(0)
    }

    private fun calculateCenterInsideVertices(viewWidth: Int, viewHeight: Int) {
        val bw = bitmap.width.toFloat()
        val bh = bitmap.height.toFloat()
        val scale = minOf(viewWidth / bw, viewHeight / bh, 1.0f)
        val dw = bw * scale
        val dh = bh * scale
        val nw = dw / viewWidth
        val nh = dh / viewHeight
        displayVertices = floatArrayOf(
            -nw, nh,
            -nw, -nh,
            nw, nh,
            nw, -nh
        )
    }

    private fun allocateFloatBuffer(arr: FloatArray): FloatBuffer =
        ByteBuffer.allocateDirect(arr.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(arr).position(0) }

    // ... (保留原有 loadShader, createProgram, loadTexture)
}
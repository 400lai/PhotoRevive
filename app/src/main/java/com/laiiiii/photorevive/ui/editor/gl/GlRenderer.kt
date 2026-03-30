package com.laiiiii.photorevive.ui.editor.gl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.laiiiii.photorevive.ui.editor.model.TransformState
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * OpenGL ES 图像渲染器
 * 协调渲染流程，管理变换状态和裁剪框
 *
 * @param context Android 上下文环境，用于加载 GLSL 着色器资源
 * @param bitmap 要渲染的源位图对象
 */
class GlRenderer(private val context: Context, private val bitmap: Bitmap) : GLSurfaceView.Renderer {
    // 图像渲染着色器程序：从外部 GLSL 文件加载顶点和片段着色器
    private val imageShader = ShaderProgram.loadFromResource(context)
    private val cropBoxRenderer = CropBoxRenderer(context)

    private var textureId = -1
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var displayVertices = FloatArray(8)

    // 定义矩形的四个顶点坐标（归一化设备坐标）
    private val vertices = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, 1f,
        1f, -1f
    )

    // 纹理坐标：左上、左下、右上、右下
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

    /**
     * 当 GLSurfaceView 创建时调用
     * 初始化 OpenGL ES 环境、编译着色器程序、加载纹理
     *
     * @param gl GL10 对象（未使用）
     * @param config EGL 配置对象（未使用）
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        vertexBuffer = BufferUtils.allocateFloatBuffer(vertices)
        textureBuffer = BufferUtils.allocateFloatBuffer(textureCoords)

        val imageShaderCreated = imageShader.create()

        if (!imageShaderCreated) {
            return
        }

        textureId = TextureLoader.loadTexture(bitmap)
    }

    /**
     * 当 GLSurfaceView 尺寸改变时调用
     * 设置视口大小，计算图像的初始缩放比例和显示区域
     *
     * @param gl GL10 对象（未使用）
     * @param width 新的视图宽度（像素）
     * @param height 新的视图高度（像素）
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        viewWidth = width
        viewHeight = height
        cropBoxRenderer.setViewSize(width, height)

        val bitmapW = bitmap.width.toFloat()
        val bitmapH = bitmap.height.toFloat()
        val wScale = width / bitmapW
        val hScale = height / bitmapH
        initialScale = minOf(wScale, hScale, 1.0f)

        calculateCenterInsideVertices(width, height)
        vertexBuffer.put(displayVertices)
        vertexBuffer.position(0)

        cropBoxRenderer.updateCropBoxFromRectF(0f, 0f, width.toFloat(), height.toFloat())
    }

    /**
     * 每帧渲染时调用
     * 清除屏幕并绘制图像和裁剪框
     *
     * @param gl GL10 对象（未使用）
     */
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        drawImage()
        cropBoxRenderer.draw()
    }

    /**
     * 绘制图像到屏幕上
     * 使用图像着色器和纹理进行渲染
     */
    private fun drawImage() {
        if (imageShader.programId == 0 || textureId == -1) return

        imageShader.use()
        bindVertexAttributes()
        bindTexture()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    /**
     * 绑定顶点属性指针
     * 设置顶点位置和纹理坐标的缓冲区数据
     */
    private fun bindVertexAttributes() {
        val posHandle = imageShader.getAttribLocation("aPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val texHandle = imageShader.getAttribLocation("aTexCoord")
        GLES20.glEnableVertexAttribArray(texHandle)
        GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
    }

    /**
     * 绑定纹理到着色器
     * 激活纹理单元并将纹理 ID 传递给片段着色器
     */
    private fun bindTexture() {
        TextureLoader.activateTextureUnit(GLES20.GL_TEXTURE0)
        TextureLoader.bindTexture(textureId)
        val uTex = imageShader.getUniformLocation("uTexture")
        imageShader.setUniform1i(uTex, 0)
    }

    /**
     * 更新图像的变换状态
     * 应用新的缩放和平移参数
     *
     * @param transformState 包含缩放比例和平移距离的变换状态对象
     */
    fun updateTransform(transformState: TransformState) {
        this.transformState = transformState
        updateVerticesWithTransform()
    }

    /**
     * 设置裁剪框区域
     * 将视图坐标系中的矩形转换为归一化设备坐标
     *
     * @param viewRect 视图坐标系中的裁剪区域矩形（left, top, right, bottom）
     */
    fun setCropBox(viewRect: RectF) {
        cropBoxRenderer.updateCropBoxFromRectF(viewRect.left, viewRect.top, viewRect.right, viewRect.bottom)
    }

    /**
     * 根据变换状态更新顶点坐标
     * 应用缩放和平移变换到图像的四个顶点
     */
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

    /**
     * 计算图像居中显示的顶点坐标
     * 保持图像宽高比，确保图像完整显示在视图内
     *
     * @param viewWidth 视图宽度（像素）
     * @param viewHeight 视图高度（像素）
     */
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

    /**
     * 释放所有 OpenGL 资源
     * 删除纹理和着色器程序，防止内存泄漏
     */
    fun release() {
        TextureLoader.deleteTexture(textureId)
        imageShader.release()
        cropBoxRenderer.release()
    }
}
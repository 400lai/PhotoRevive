package com.laiiiii.photorevive.ui.editor.gl

import android.content.Context
import android.opengl.GLES20
import java.nio.FloatBuffer

/**
 * 裁剪框渲染器，专门处理裁剪框的渲染逻辑
 */
class CropBoxRenderer(private val context: Context) {
    private val cropBoxShader = ShaderProgram.createCropBoxShaderFromResource(context)
    private var cropVertexBuffer: FloatBuffer? = null
    private var cropBoxNormalized = floatArrayOf(-1f, 1f, 1f, -1f)
    private var viewWidth = 0
    private var viewHeight = 0

    /**
     * 设置视图尺寸，用于坐标转换
     *
     * @param width 视图宽度
     * @param height 视图高度
     */
    fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    /**
     * 根据视图坐标更新裁剪框的归一化坐标
     *
     * @param left 左边界像素坐标
     * @param top 上边界像素坐标
     * @param right 右边界像素坐标
     * @param bottom 下边界像素坐标
     */
    fun updateCropBoxFromRectF(left: Float, top: Float, right: Float, bottom: Float) {
        if (viewWidth == 0 || viewHeight == 0) return

        val x1 = (left / viewWidth) * 2f - 1f
        val y1 = 1f - (top / viewHeight) * 2f
        val x2 = (right / viewWidth) * 2f - 1f
        val y2 = 1f - (bottom / viewHeight) * 2f

        cropBoxNormalized = floatArrayOf(
            x1, y1,
            x1, y2,
            x2, y2,
            x2, y1
        )
        cropVertexBuffer = BufferUtils.allocateFloatBuffer(cropBoxNormalized)
    }

    /**
     * 绘制裁剪框线框
     */
    fun draw() {
        if (cropBoxShader.programId == 0 || cropVertexBuffer == null) return

        cropBoxShader.use()
        val posHandle = cropBoxShader.getAttribLocation("aPosition")

        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, cropVertexBuffer)

        GLES20.glLineWidth(3.0f)
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4)

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    /**
     * 释放裁剪框渲染器资源
     */
    fun release() {
        cropBoxShader.release()
    }
}
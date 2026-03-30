package com.laiiiii.photorevive.ui.editor.gl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

/**
 * OpenGL ES 纹理加载器
 * 负责将 Bitmap 加载到 GPU 纹理
 */
object TextureLoader {

    /**
     * 从位图加载 OpenGL 纹理
     *
     * @param bitmap 要加载的位图对象
     * @return 纹理 ID，失败返回 -1
     */
    fun loadTexture(bitmap: Bitmap): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        val textureId = textures[0]

        if (textureId == 0) return -1

        bindTexture(textureId)
        setTextureParameters()
        uploadBitmap(bitmap)

        return textureId
    }

    /**
     * 绑定纹理到目标
     */
    fun bindTexture(textureId: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
    }

    /**
     * 激活纹理单元
     */
    fun activateTextureUnit(unit: Int = GLES20.GL_TEXTURE0) {
        GLES20.glActiveTexture(unit)
    }

    /**
     * 删除纹理
     */
    fun deleteTexture(textureId: Int) {
        if (textureId != -1) {
            val textures = intArrayOf(textureId)
            GLES20.glDeleteTextures(1, textures, 0)
        }
    }

    private fun setTextureParameters() {
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }
}

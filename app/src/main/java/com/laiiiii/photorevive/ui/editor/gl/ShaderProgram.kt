package com.laiiiii.photorevive.ui.editor.gl

import android.content.Context
import android.opengl.GLES20
import androidx.annotation.RawRes
import com.laiiiii.photorevive.R

/**
 * 管理 OpenGL ES 着色器程序
 * 支持创建、编译和链接顶点/片段着色器
 */
class ShaderProgram(
    private val vertexShaderCode: String,
    private val fragmentShaderCode: String
) {
    var programId = 0
        private set

    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    /**
     * 创建并编译着色器程序
     * @return 是否创建成功
     */
    fun create(): Boolean {
        vertexShaderId = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        if (vertexShaderId == 0) return false

        fragmentShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        if (fragmentShaderId == 0) {
            GLES20.glDeleteShader(vertexShaderId)
            return false
        }

        programId = GLES20.glCreateProgram()
        if (programId == 0) {
            GLES20.glDeleteShader(vertexShaderId)
            GLES20.glDeleteShader(fragmentShaderId)
            return false
        }

        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)
        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(programId)
            programId = 0
            return false
        }

        return true
    }

    /**
     * 使用此着色器程序
     */
    fun use() {
        if (programId != 0) {
            GLES20.glUseProgram(programId)
        }
    }

    /**
     * 获取属性位置
     */
    fun getAttribLocation(name: String): Int =
        if (programId != 0) GLES20.glGetAttribLocation(programId, name) else -1

    /**
     * 获取 Uniform 位置
     */
    fun getUniformLocation(name: String): Int =
        if (programId != 0) GLES20.glGetUniformLocation(programId, name) else -1

    /**
     * 设置 Uniform int 值
     */
    fun setUniform1i(location: Int, value: Int) {
        if (programId != 0 && location >= 0) {
            GLES20.glUniform1i(location, value)
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        if (programId != 0) {
            GLES20.glDeleteProgram(programId)
            programId = 0
        }
        if (vertexShaderId != 0) {
            GLES20.glDeleteShader(vertexShaderId)
            vertexShaderId = 0
        }
        if (fragmentShaderId != 0) {
            GLES20.glDeleteShader(fragmentShaderId)
            fragmentShaderId = 0
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) return 0

        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteShader(shader)
            return 0
        }

        return shader
    }

    companion object {
        fun loadFromResource(context: Context): ShaderProgram {
            val vertexShaderCode = loadShaderFromResource(context, R.raw.vertex_shader)
            val fragmentShaderCode = loadShaderFromResource(context, R.raw.fragment_shader)
            return ShaderProgram(vertexShaderCode, fragmentShaderCode)
        }

        fun createCropBoxShaderFromResource(context: Context): ShaderProgram {
            val vertexShaderCode = loadShaderFromResource(context, R.raw.crop_box_vertex_shader)
            val fragmentShaderCode = loadShaderFromResource(context, R.raw.crop_box_fragment_shader)
            return ShaderProgram(vertexShaderCode, fragmentShaderCode)
        }

        private fun loadShaderFromResource(context: Context, @RawRes resourceId: Int): String {
            return context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        }
    }
}

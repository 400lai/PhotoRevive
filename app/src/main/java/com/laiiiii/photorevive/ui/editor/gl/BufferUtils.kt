package com.laiiiii.photorevive.ui.editor.gl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 缓冲区工具类，提供统一的缓冲区创建方法
 */
object BufferUtils {
    /**
     * 创建并初始化 FloatBuffer
     *
     * @param arr 要存储的浮点数数组
     * @return 包含数据的 FloatBuffer 对象
     */
    fun allocateFloatBuffer(arr: FloatArray): FloatBuffer =
        ByteBuffer.allocateDirect(arr.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(arr).position(0) }
}
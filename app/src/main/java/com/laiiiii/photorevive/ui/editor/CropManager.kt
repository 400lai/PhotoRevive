// ui/editor/CropManager.kt
package com.laiiiii.photorevive.ui.editor

import android.graphics.RectF

data class CropState(
    val isActive: Boolean = false,
    val aspectRatio: Float? = null, // null 表示自由裁剪
    val cropRect: RectF = RectF()
)

object CropManager {
    private const val EPSILON = 0.001f

    fun calculateInitialCropRect(viewWidth: Int, viewHeight: Int, bitmapWidth: Int, bitmapHeight: Int): RectF {
        // 初始裁剪为整个视图尺寸（对应 bitmap 的显示区域）
        // 但注意：cropRect 是基于 **view 坐标系** 的，不是 bitmap 像素坐标！
        // 所以应返回 [0, 0, viewWidth, viewHeight]
        return RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
    }

    fun applyAspectRatio(cropRect: RectF, aspectRatio: Float?, viewWidth: Int, viewHeight: Int): RectF {
        if (aspectRatio == null) return RectF(cropRect)

        val currentWidth = cropRect.width()
        val currentHeight = cropRect.height()
        val currentAspect = currentWidth / currentHeight

        val newRect = RectF(cropRect)
        if (Math.abs(currentAspect - aspectRatio) > EPSILON) {
            if (currentAspect > aspectRatio) {
                // 宽度太大 → 缩小宽度
                val newWidth = currentHeight * aspectRatio
                val diff = currentWidth - newWidth
                newRect.left += diff / 2f
                newRect.right -= diff / 2f
            } else {
                // 高度太大 → 缩小高度
                val newHeight = currentWidth / aspectRatio
                val diff = currentHeight - newHeight
                newRect.top += diff / 2f
                newRect.bottom -= diff / 2f
            }
        }

        // 确保不超出边界
        newRect.left = newRect.left.coerceAtLeast(0f)
        newRect.top = newRect.top.coerceAtLeast(0f)
        newRect.right = newRect.right.coerceAtMost(viewWidth.toFloat())
        newRect.bottom = newRect.bottom.coerceAtMost(viewHeight.toFloat())

        return newRect
    }
}
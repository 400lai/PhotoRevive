// ui/editor/EditorTouchListener.kt
package com.laiiiii.photorevive.ui.editor

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.laiiiii.photorevive.ui.editor.model.TransformState

class EditorTouchListener(
    private val onTransformChanged: (TransformState) -> Unit
) {
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var transformState = TransformState.DEFAULT

    fun onTouchEvent(event: MotionEvent, scaleDetector: ScaleGestureDetector): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                activePointerId = event.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // 只有在没有进行缩放时才处理平移
                if (!scaleDetector.isInProgress) {
                    val pointerIndex = event.findPointerIndex(activePointerId)
                    if (pointerIndex >= 0) {
                        val x = event.getX(pointerIndex)
                        val y = event.getY(pointerIndex)

                        // 计算移动距离
                        val dx = x - lastTouchX
                        val dy = y - lastTouchY

                        // 更新变换状态
                        transformState = transformState.copy(
                            translateX = transformState.translateX + dx,
                            translateY = transformState.translateY + dy
                        )

                        onTransformChanged(transformState)

                        lastTouchX = x
                        lastTouchY = y
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = if (event.actionIndex == 0) 1 else 0
                lastTouchX = event.getX(pointerIndex)
                lastTouchY = event.getY(pointerIndex)
                activePointerId = event.getPointerId(pointerIndex)
            }
        }

        return true
    }

    // 缩放监听器
    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor

            // 限制缩放范围，避免过度缩放
            val constrainedScaleFactor = when {
                scaleFactor > 1.1f -> 1.1f
                scaleFactor < 0.9f -> 0.9f
                else -> scaleFactor
            }

            transformState = transformState.copy(
                scaleX = transformState.scaleX * constrainedScaleFactor,
                scaleY = transformState.scaleY * constrainedScaleFactor
            )
            onTransformChanged(transformState)
            return true
        }
    }

    fun resetTransform() {
        transformState = TransformState.DEFAULT
        onTransformChanged(transformState)
    }

    fun getCurrentTransform(): TransformState = transformState
}

// ui/editor/GLSurfaceView.kt
package com.laiiiii.photorevive.ui.editor

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class GLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    init {
        setEGLContextClientVersion(2)
    }

    private var touchListener: EditorTouchListener? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    fun setEditorTouchListener(listener: EditorTouchListener) {
        this.touchListener = listener
        scaleGestureDetector = ScaleGestureDetector(context, listener.ScaleListener())

        super.setOnTouchListener { _, event ->
            touchListener?.onTouchEvent(event, scaleGestureDetector) ?: false
        }
    }
}

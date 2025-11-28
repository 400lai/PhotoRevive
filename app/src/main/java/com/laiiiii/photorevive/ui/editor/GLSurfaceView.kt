package com.laiiiii.photorevive.ui.editor

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    init {
        setEGLContextClientVersion(2)
    }
}
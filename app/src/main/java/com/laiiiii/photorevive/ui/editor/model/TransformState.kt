package com.laiiiii.photorevive.ui.editor.model

data class TransformState(
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val translateX: Float = 0.0f,
    val translateY: Float = 0.0f,
    val rotation: Float = 0.0f
) {
    companion object {
        val DEFAULT = TransformState()
    }

    fun reset(): TransformState {
        return TransformState()
    }
}

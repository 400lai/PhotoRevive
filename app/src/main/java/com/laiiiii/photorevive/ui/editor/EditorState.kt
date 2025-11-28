package com.laiiiii.photorevive.ui.editor

import com.laiiiii.photorevive.ui.editor.model.TransformState

sealed class EditorState {
    object Loading : EditorState()
    data class Ready(val transformState: TransformState = TransformState.DEFAULT) : EditorState()
    data class Error(val message: String) : EditorState()
}

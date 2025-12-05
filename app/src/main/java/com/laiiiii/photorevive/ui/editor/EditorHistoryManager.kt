package com.laiiiii.photorevive.ui.editor

import com.laiiiii.photorevive.ui.editor.model.TransformState

data class EditorSnapshot(
    val transform: TransformState,
    val crop: CropState
)

class EditorHistoryManager {
    private val history = mutableListOf<EditorSnapshot>()
    private var currentIndex = -1

    fun saveSnapshot(transform: TransformState, crop: CropState) {
        // 如果有 redo 历史，先清除
        if (currentIndex < history.size - 1) {
            history.subList(currentIndex + 1, history.size).clear()
        }

        history.add(EditorSnapshot(transform, crop))
        currentIndex = history.lastIndex
    }

    fun canUndo(): Boolean = currentIndex > 0
    fun canRedo(): Boolean = currentIndex < history.size - 1

    fun undo(): EditorSnapshot? {
        if (!canUndo()) return null
        currentIndex--
        return history[currentIndex]
    }

    fun redo(): EditorSnapshot? {
        if (!canRedo()) return null
        currentIndex++
        return history[currentIndex]
    }

    fun getCurrent(): EditorSnapshot? {
        return if (history.isNotEmpty()) history[currentIndex] else null
    }

    fun clear() {
        history.clear()
        currentIndex = -1
    }
}
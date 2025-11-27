package com.laiiiii.photorevive.ui.recyclerview.model

data class MediaItem(
    val id: Long,
    val uri: String,
    val isVideo: Boolean = false
)
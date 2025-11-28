package com.laiiiii.photorevive.ui.editor

import android.net.Uri

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val uri: Uri) : ExportState()
    data class Error(val message: String) : ExportState()
}

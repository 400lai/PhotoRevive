package com.laiiiii.photorevive.ui.recyclerview

import android.app.Application
import android.os.Trace
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.laiiiii.photorevive.data.repository.LocalMediaRepository
import com.laiiiii.photorevive.ui.recyclerview.model.MediaItem
import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {

    private val _mediaItems = MutableLiveData<List<MediaItem>>()
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    fun loadMedia() {
        viewModelScope.launch {
            Trace.beginSection("ViewModelLoadMedia")
            val items = LocalMediaRepository.loadImagesFromDevice(getApplication())
            _mediaItems.postValue(items)
            Trace.endSection() // ViewModelLoadMedia 加载完成
        }
    }
}
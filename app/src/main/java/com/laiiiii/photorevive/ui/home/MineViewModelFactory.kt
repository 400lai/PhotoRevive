package com.laiiiii.photorevive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.repository.UserRepository

class MineViewModelFactory(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MineViewModel(userRepository, appPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.laiiiii.photorevive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.local.User
import com.laiiiii.photorevive.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MineViewModel(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val userId = appPreferences.currentUserId.firstOrNull() ?: return@launch
            val user = userRepository.getCurrentUser()
            _user.value = user
            _isLoggedIn.value = appPreferences.isLoggedIn.firstOrNull() ?: false
        }
    }

    fun login(account: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.loginUser(account, password)
                if (result.isSuccess) {
                    _isLoggedIn.value = true
                    loadCurrentUser()
                } else {
                    // 处理失败
                }
            } catch (e: Exception) {
                // 错误处理
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            appPreferences.setLoggedOut()
            _isLoggedIn.value = false
            _user.value = null
        }
    }
}
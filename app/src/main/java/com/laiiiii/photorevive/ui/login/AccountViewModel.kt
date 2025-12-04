package com.laiiiii.photorevive.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laiiiii.photorevive.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<ResultState<Long>>(ResultState.Idle)
    val loginResult = _loginResult.asStateFlow()

    fun login(account: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading
            try {
                val result = userRepository.loginUser(account, password)
                _loginResult.value = ResultState.Success(result.getOrThrow())
            } catch (e: Exception) {
                _loginResult.value = ResultState.Error(e.message ?: "登录失败")
            }
        }
    }

    sealed interface ResultState<out T> {
        object Idle : ResultState<Nothing>
        object Loading : ResultState<Nothing>
        data class Success<T>(val data: T) : ResultState<T>
        data class Error(val message: String) : ResultState<Nothing>
    }
}
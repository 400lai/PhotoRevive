package com.laiiiii.photorevive.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.local.UserDatabase
import com.laiiiii.photorevive.data.repository.UserRepository


class AccountManager : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = UserDatabase.getDatabase(this)
        val appPrefs = AppPreferences(this)
        val repo = UserRepository(db.userDao(), appPrefs)
        val viewModel = AccountViewModel(repo)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccountScreen(
                        viewModel = viewModel,
                        onBack = { finish() },
                        onLoginSuccess = {
                            // 处理登录成功逻辑
                        },
                        onPhoneLogin = {
                            // 处理手机号登录
                        },
                        onOtherLogin = {
                            // 处理其他方式登录
                        },
                        onRegister = {
                            // 处理注册
                        },
                        onMore = {
                            // 处理更多选项
                        }
                    )
                }
            }
        }
    }
}

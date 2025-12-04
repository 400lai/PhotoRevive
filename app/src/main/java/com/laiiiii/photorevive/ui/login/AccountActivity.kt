package com.laiiiii.photorevive.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.laiiiii.photorevive.MainActivity
import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.local.UserDatabase
import com.laiiiii.photorevive.data.repository.UserRepository

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建依赖项
        val db = UserDatabase.getDatabase(this)
        val appPrefs = AppPreferences(this)
        val repo = UserRepository(db.userDao(), appPrefs)

        // 创建 ViewModel
        val viewModel = AccountViewModel(repo)

        setContent {
            MaterialTheme {
                AccountScreen(
                    viewModel = viewModel,
                    onBack = { finish() },
                    onLoginSuccess = {
                        setResult(RESULT_OK)
                        // 添加跳转到主界面的逻辑
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
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
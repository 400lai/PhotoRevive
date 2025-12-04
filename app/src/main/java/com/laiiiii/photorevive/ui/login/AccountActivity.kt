package com.laiiiii.photorevive.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AccountScreen(
                    onBack = { finish() },
                    onLogin = { username, password ->
                        // 处理登录逻辑
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

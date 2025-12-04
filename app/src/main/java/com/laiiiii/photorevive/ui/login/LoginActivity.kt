package com.laiiiii.photorevive.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreenWrapper()
        }
    }

    @Composable
    fun LoginScreenWrapper() {
        LoginScreen(
            onDismiss = { finish() },  // 点击关闭时结束当前Activity，返回上一页
            onAgreeChange = {},
            onExistingAccountClick = {},
            onRecoverAccountClick = {},
            onLoginClick = {  // 添加登录按钮点击回调
                val intent = Intent(this@LoginActivity, AccountActivity::class.java)
                startActivity(intent)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginScreen(
        onDismiss = {},
        onAgreeChange = {},
        onExistingAccountClick = {},
        onRecoverAccountClick = {},
        onLoginClick = {}  // 预览中添加空实现
    )
}

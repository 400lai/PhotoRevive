package com.laiiiii.photorevive.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 确保系统 UI 可见
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE


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
            onRecoverAccountClick = {}
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
        onRecoverAccountClick = {}
    )
}

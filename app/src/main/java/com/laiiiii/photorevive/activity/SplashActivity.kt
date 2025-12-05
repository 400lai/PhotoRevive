package com.laiiiii.photorevive.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.laiiiii.photorevive.MainActivity
import com.laiiiii.photorevive.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 模拟启动延迟
        // Handler显示传入 Looper
        Handler(Looper.getMainLooper()).postDelayed({
            // 启动主页面 MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // 关闭启动页面
            finish()
        }, 2000)    // 2秒延迟
    }
}
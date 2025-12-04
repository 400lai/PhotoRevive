package com.laiiiii.photorevive.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AccountScreen(
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onPhoneLogin: () -> Unit = {},
    onOtherLogin: () -> Unit = {},
    onRegister: () -> Unit = {},
    onMore: () -> Unit = {},
    viewModel: AccountViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAgreed by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 监听登录结果
    val loginResult by viewModel.loginResult.collectAsState()

    // 处理登录结果
    when (val result = loginResult) {
        is AccountViewModel.ResultState.Success -> {
            // 显示成功消息并调用成功回调
            androidx.compose.runtime.LaunchedEffect(result) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "登录成功",
                    actionLabel = "确定"
                )
                when (snackbarResult) {
                    SnackbarResult.ActionPerformed,
                    SnackbarResult.Dismissed -> {
                        onLoginSuccess()
                    }
                }
            }
        }
        is AccountViewModel.ResultState.Error -> {
            // 显示错误消息
            androidx.compose.runtime.LaunchedEffect(result) {
                snackbarHostState.showSnackbar(
                    message = result.message,
                    actionLabel = "确定"
                )
            }
        }
        else -> {
            // 其他状态不做特殊处理
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 返回按钮
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Logo 区域 - "醒图"
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "醒图",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(80.dp))

        // 输入框区域
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (usernameError && it.isNotBlank()) usernameError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (usernameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(  // 使用新的 API
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = if (usernameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (usernameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                ),
                placeholder = { Text("输入账号") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                isError = usernameError
            )

            if (usernameError) {
                Text(
                    text = "请输入用户名",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError && it.isNotBlank()) passwordError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(  // 使用新的 API
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = if (passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (passwordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                ),
                placeholder = { Text("输入密码") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                isError = passwordError
            )

            if (passwordError) {
                Text(
                    text = "请输入密码",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isLoginEnabled = username.isNotBlank() && password.isNotBlank() && isAgreed

            Button(
                onClick = {
                    usernameError = username.isBlank()
                    passwordError = password.isBlank()

                    if (!usernameError && !passwordError && isAgreed) {
                        // 调用viewModel的登录方法
                        viewModel.login(username, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = isLoginEnabled
            ) {
                Text(
                    text = "登录",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAgreed,
                    onCheckedChange = { isAgreed = it },
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "已阅读并同意",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "服务协议",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        // TODO: 打开服务协议页面
                    }
                )
                Text(
                    text = "和",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "隐私保护指引",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        // TODO: 打开隐私政策页面
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部快捷按钮
        Box(modifier = Modifier.weight(1f)) {
            // 底部快捷按钮
            Column {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 手机号登录
                    BottomActionButton(
                        icon = Icons.Default.Phone,
                        text = "手机号登录",
                        onClick = onPhoneLogin
                    )

                    // 其他方式登录
                    BottomActionButton(
                        icon = Icons.Default.Person,
                        text = "其他方式登录",
                        onClick = onOtherLogin
                    )

                    // 注册
                    BottomActionButton(
                        icon = Icons.Default.Add,
                        text = "注册",
                        onClick = onRegister
                    )

                    // 更多
                    BottomActionButton(
                        icon = Icons.Default.MoreVert,
                        text = "更多",
                        onClick = onMore
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 添加 SnackbarHost
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = if (snackbarData.visuals.message == "登录成功") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        }
    }
}

@Composable
fun BottomActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}



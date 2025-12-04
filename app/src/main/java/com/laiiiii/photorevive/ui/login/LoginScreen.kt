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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onDismiss: () -> Unit,
    onAgreeChange: (Boolean) -> Unit,
    onExistingAccountClick: () -> Unit,
    onRecoverAccountClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isChecked by remember { mutableStateOf(false) }
    var showAgreementDialog by remember { mutableStateOf(false) }

    // 处理点击新用户/登录按钮逻辑
    val handleNewUserClick = {
        if (!isChecked) {
            showAgreementDialog = true
        } else {
            // TODO: 新用户注册逻辑
        }
    }

    val handleLoginClick = {
        if (!isChecked) {
            showAgreementDialog = true
        } else {
            onLoginClick()
            // TODO: 登录逻辑
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: show help dialog */ }) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color.Green.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(4.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Green.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50.dp)
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "醒图",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(160.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        onClick = handleNewUserClick
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "New User",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "新用户",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        onClick = handleLoginClick
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Login",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "登录",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            onAgreeChange(it)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "已阅读并同意 用户协议 和 隐私协议",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "原有账号登录",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.clickable { onExistingAccountClick() }
                    )
                    Text(
                        text = " 或 ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "找回账号",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.clickable { onRecoverAccountClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "温馨提示：账号仅支持3台设备同时登录",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // 协议弹窗
        if (showAgreementDialog) {
            AlertDialog(
                onDismissRequest = { showAgreementDialog = false },
                title = { Text("用户协议及隐私政策") },
                text = {
                    Text(
                        text = "已阅读并同意 用户协议 和 隐私协议，\n同时登录并使用醒图和抖音，运营商将对你提供的手机号进行验证",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAgreementDialog = false
                            isChecked = true
                            onAgreeChange(true)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("同意并登录", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAgreementDialog = false }) {
                        Text("不同意")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                properties = DialogProperties(
                    dismissOnClickOutside = false
                )
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        Surface {
            LoginScreen(
                onDismiss = {},
                onAgreeChange = {},
                onExistingAccountClick = {},
                onRecoverAccountClick = {}
            )
        }
    }
}
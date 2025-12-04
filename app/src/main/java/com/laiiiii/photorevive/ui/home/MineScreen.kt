package com.laiiiii.photorevive.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laiiiii.photorevive.R
import com.laiiiii.photorevive.data.local.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    viewModel: MineViewModel,
    onLoginClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    onCreateClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { },
            actions = {
                IconButton(onClick = { /* 打开侧边栏 */ }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
                IconButton(onClick = { /* 打开帮助 */ }) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = "Help")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            )
        )

        if (isLoggedIn) {
            // 登录后界面
            LoggedInUserSection(user = user, onEditProfileClick = onEditProfileClick)
        } else {
            // 未登录界面
            NotLoggedInSection(onLoginClick = onLoginClick)
        }

        // VIP 横幅（无论登录与否都显示）
        VIPBanner(onSubscribeClick = onSubscribeClick)

        // AI 分身卡片
        AIAvatarCard(onCreateClick = onCreateClick)

        // 作品集标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "作品集",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
        }
        HorizontalDivider(
            modifier = Modifier.height(1.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        )

        // 作品集内容区
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "暂无作品集",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "去创作作品吧 >",
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onCreateClick() }
                )
            }
        }
    }
}

// =================== 组件拆分 ===================

@Composable
private fun NotLoggedInSection(onLoginClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .clickable { onLoginClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Default.Person,
            contentDescription = "User Avatar",
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "立即登录",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "登录查看更多互动消息",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }

}

@Composable
private fun LoggedInUserSection(
    user: User?,
    onEditProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .clickable { onEditProfileClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = user?.username ?: "用户${user?.userId}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ID: ${user?.userId}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }

    // 用户统计
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${user?.followCount ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "关注", fontSize = 12.sp, color = Color.Gray)
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${user?.followerCount ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "粉丝", fontSize = 12.sp, color = Color.Gray)
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${user?.usageCount ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "总使用量", fontSize = 12.sp, color = Color.Gray)
        }
        Button(
            onClick = onEditProfileClick,
            modifier = Modifier.padding(start = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("编辑资料", fontSize = 14.sp)
        }
    }

    // 个人简介
    if (user?.bio.isNullOrEmpty()) {
        Text(
            text = "你还没有填写个人简介，点击添加...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )
    } else {
        Text(
            text = user?.bio ?: "暂无个人简介",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun VIPBanner(onSubscribeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(72.dp)
            .background(color = Color(0xFFB9FF66))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "VIP",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VIP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "限时1折畅享会员权益，轻松修出高级感",
                    modifier = Modifier.padding(start = 24.dp, top = 4.dp),
                    fontSize = 11.sp,
                    color = Color.Black
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onSubscribeClick,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "立即订阅",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AIAvatarCard(onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "AI Avatar",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "我的AI分身",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "创建分身 >",
                color = Color(0xFF666666),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onCreateClick() }
            )
        }
    }
}




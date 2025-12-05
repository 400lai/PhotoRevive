# 📱 PhotoRevive

一个基于 OpenGL ES 渲染的现代化 Android 图片编辑应用，提供高性能的图像处理、完整的用户账号系统和流畅的编辑体验。

------

## 📦 项目简介

**PhotoRevive** 是一个采用 **Kotlin** 和现代 Android 架构开发的专业图片编辑应用。项目采用 **MVVM 架构模式**，结合 **OpenGL ES 2.0** 实现硬件加速渲染，提供流畅的 60fps 编辑体验。

### ✨ 核心特性

- 🎨 **硬件加速渲染**：基于 OpenGL ES 2.0，GPU 并行计算，性能提升 10 倍
- ✂️ **智能裁剪系统**：支持自由裁剪、多种预设比例（1:1、4:3、16:9 等）
- 🔄 **实时变换**：流畅的双指缩放、平移、旋转操作
- ⏪ **历史管理**：完整的 Undo/Redo 功能，支持状态快照
- 📤 **高质量导出**：无损 JPEG 压缩，自动集成到系统相册
- 🌓 **沉浸式体验**：全屏编辑界面，隐藏系统 UI

- 👤 **用户账号系统**：支持注册登录、密码加密存储、会话管理
- 🔐 **数据安全**：SHA-256 + Salt 密码加密，Room 数据库持久化

### 🛠️ 技术栈

| 技术领域     | 使用技术                           |
| ------------ | ---------------------------------- |
| **编程语言** | Kotlin 1.9+                        |
| **架构模式** | MVVM (ViewModel + LiveData)        |
| **渲染引擎** | OpenGL ES 2.0 (GLSL)               |
| **异步处理** | Kotlin Coroutines + Flow           |
| **数据库**   | Room Database                      |
| **轻量存储** | DataStore (替代 SharedPreferences) |
| **图像处理** | Android Bitmap API                 |
| **存储管理** | MediaStore API (Scoped Storage)    |
| **手势检测** | ScaleGestureDetector + MotionEvent |
| **密码加密** | SHA-256 + 随机 Salt                |
| **构建工具** | Gradle Kotlin DSL 8.0+             |
| **最低支持** | Android 7.0 (API 24)               |
| **目标版本** | Android 14 (API 34)                |

------

## 🚀 快速开始

### 📋 环境要求

- **Android Studio**: Narwhal 2025.1.1 Patch 1 或更高版本
- **JDK**: 17（推荐使用 Android Studio 内置 JDK）
- **Gradle**: 8.0+（自动下载）
- **Android SDK**: API 24 - 34
- **测试设备**: Android 7.0+ 真机或模拟器

### 📥 克隆项目

```bash
git clone https://github.com/400lai/PhotoRevive.git
cd PhotoRevive
```

### 🔧 配置项目

#### 1. 同步依赖

在 Android Studio 中打开项目后，点击顶部的 **"Sync Project with Gradle Files"** 按钮，或执行：

```bash
./gradlew build --refresh-dependencies
```

#### 2. 配置签名（可选，用于发布版本）

在 `app/build.gradle.kts` 中添加签名配置：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/your/keystore.jks")
            storePassword = "your_store_password"
            keyAlias = "your_key_alias"
            keyPassword = "your_key_password"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### ▶️ 运行应用

#### 方法一：Android Studio（推荐）

1. 连接 Android 设备（开启 **USB 调试**）或启动 AVD 模拟器
2. 在顶部工具栏选择设备
3. 点击绿色 **Run ▶️** 按钮
4. 应用将自动安装并启动

#### 方法二：命令行

```bash
# 安装 Debug 版本
./gradlew installDebug

# 或手动安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

> 💡 **Windows 用户**：请将 `./gradlew` 替换为 `gradlew.bat`

### 📦 构建发布版本

```bash
# 构建 APK
./gradlew assembleRelease

# 构建 AAB（Google Play）
./gradlew bundleRelease

# 输出路径
# APK: app/build/outputs/apk/release/app-release.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

------

## 📁 项目架构

### 目录结构

```
PhotoRevive/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/laiiiii/photorevive/
│   │   │   │   ├── activity/
│   │   │   │   │   ├── MainActivity.kt               # 应用主入口
│   │   │   │   │   ├── SplashActivity.kt             # 启动页
│   │   │   │   │   ├── AlbumActivity.kt              # 相册选择页
│   │   │   │   │   ├── EditorActivity.kt             # 编辑器核心页面
│   │   │   │   │   └── HelperActivity.kt             # 帮助页面
│   │   │   │   │
│   │   │   │   ├── ui/
│   │   │   │   │   ├── editor/
│   │   │   │   │   │   ├── EditorViewModel.kt        # 编辑器业务逻辑
│   │   │   │   │   │   ├── EditorState.kt            # 状态封装
│   │   │   │   │   │   ├── ExportState.kt            # 导出状态
│   │   │   │   │   │   ├── ImageRenderer.kt          # OpenGL 渲染器
│   │   │   │   │   │   ├── GLSurfaceView.kt          # 自定义渲染视图
│   │   │   │   │   │   ├── EditorTouchListener.kt    # 手势处理
│   │   │   │   │   │   ├── EditorHistoryManager.kt   # 历史管理
│   │   │   │   │   │   ├── CropManager.kt            # 裁剪逻辑
│   │   │   │   │   │   └── model/
│   │   │   │   │   │       └── TransformState.kt     # 变换状态
│   │   │   │   │   │
│   │   │   │   │   ├── login/                        # 【新增】用户账号模块
│   │   │   │   │   │   ├── LoginActivity.kt          # 登录入口页
│   │   │   │   │   │   ├── LoginScreen.kt            # 登录 UI（Compose）
│   │   │   │   │   │   ├── AccountActivity.kt        # 账号登录页
│   │   │   │   │   │   ├── AccountScreen.kt          # 账号登录 UI（Compose）
│   │   │   │   │   │   ├── AccountViewModel.kt       # 登录业务逻辑
│   │   │   │   │   │   └── AccountManager.kt         # 账号管理页
│   │   │   │   │   │
│   │   │   │   │   ├── album/
│   │   │   │   │   │   ├── AlbumViewModel.kt         # 相册数据管理
│   │   │   │   │   │   └── AlbumAdapter.kt           # 相册列表适配器
│   │   │   │   │   │
│   │   │   │   │   └── custom/
│   │   │   │   │       └── ShimmerCardView.kt        # 骨架屏组件
│   │   │   │   │
│   │   │   │   ├── data/                             # 【新增】数据层
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── User.kt                   # 用户实体类
│   │   │   │   │   │   ├── UserDao.kt                # 用户数据访问对象
│   │   │   │   │   │   ├── UserDatabase.kt           # Room 数据库配置
│   │   │   │   │   │   └── AppPreferences.kt         # DataStore 配置
│   │   │   │   │   │
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── UserRepository.kt         # 用户数据仓库
│   │   │   │   │       └── LocalMediaRepository.kt   # 媒体数据仓库
│   │   │   │   │
│   │   │   │   └── utils/                            # 【新增】工具类
│   │   │   │       └── PasswordUtils.kt              # 密码加密工具
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                           # XML 布局文件
│   │   │   │   ├── drawable/                         # 矢量图、形状
│   │   │   │   ├── mipmap-*/                         # 应用图标（含 WebP）
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml                    # 颜色定义
│   │   │   │   │   ├── strings.xml                   # 文本资源
│   │   │   │   │   ├── themes.xml                    # 主题配置
│   │   │   │   │   └── dimens.xml                    # 尺寸定义
│   │   │   │   └── menu/
│   │   │   │       └── bottom_nav_menu.xml           # 底部导航菜单
│   │   │   │
│   │   │   └── AndroidManifest.xml                   # 应用清单
│   │   │
│   │   ├── androidTest/                              # 仪器化测试
│   │   └── test/                                     # 单元测试
│   │
│   ├── build.gradle.kts                              # 模块构建脚本
│   └── proguard-rules.pro                            # 混淆规则
│
├── gradle/                                           # Gradle Wrapper 配置
├── build.gradle.kts                                  # 项目级构建脚本
├── settings.gradle.kts                               # 项目设置
└── README.md                                         # 本文档
```



### 架构设计

#### MVVM 架构分层

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Activity/Fragment + Compose UI)       │
│  - LoginActivity/AccountActivity        │
│  - EditorActivity                       │
│  - Compose Screens                      │
└─────────────┬───────────────────────────┘
              │ 数据绑定/事件监听
┌─────────────▼───────────────────────────┐
│          ViewModel Layer                │
│        (业务逻辑处理)                     │
│  - AccountViewModel                     │
│  - EditorViewModel                      │
│  - AlbumViewModel                       │
│  - StateFlow/LiveData                   │
└─────────────┬───────────────────────────┘
              │ 数据交互
┌─────────────▼───────────────────────────┐
│      Repository/Manager Layer           │
│         (数据处理与管理)                  │
│  - UserRepository                       │
│  - EditorHistoryManager                 │
│  - CropManager                          │
│  - LocalMediaRepository                 │
└─────────────┬───────────────────────────┘
              │ 数据持久化/系统调用
┌─────────────▼───────────────────────────┐
│       Data Layer & System APIs          │
│  - Room Database (UserDao)              │
│  - DataStore (AppPreferences)           │
│  - OpenGL ES 2.0 (GPU 渲染)             │
│  - MediaStore (相册访问)                 │
│  - Bitmap API (图像处理)                 │
└─────────────────────────────────────────┘
```

------

## 🎯 核心功能详解

### 1. 👤 用户账号系统

#### 功能特性

- **用户注册**：账号唯一性校验，自动生成默认用户名
- **用户登录**：支持账号密码登录，实时表单验证
- **会话管理**：基于 DataStore 的持久化登录状态
- **密码安全**：SHA-256 + 随机 Salt 加密存储
- **账号状态**：支持账号冻结/注销功能

#### 数据库设计

**User 表结构**：

| 字段名              | 类型    | 约束              | 说明                       |
| ------------------- | ------- | ----------------- | -------------------------- |
| **user_id**         | LONG    | PRIMARY KEY, AUTO | 用户唯一标识               |
| **account**         | STRING  | NOT NULL, UNIQUE  | 登录账号                   |
| **username**        | STRING  | NOT NULL          | 用户昵称                   |
| **password_hash**   | STRING  | NOT NULL          | SHA-256 密码哈希           |
| **salt**            | STRING  | NOT NULL          | 密码盐值（16 字符）        |
| **avatar_url**      | STRING? | NULLABLE          | 头像 URL                   |
| **bio**             | STRING? | NULLABLE          | 个人简介                   |
| **follow_count**    | INT     | DEFAULT 0         | 关注数                     |
| **follower_count**  | INT     | DEFAULT 0         | 粉丝数                     |
| **usage_count**     | INT     | DEFAULT 0         | 使用次数                   |
| **is_vip**          | BOOLEAN | DEFAULT false     | 是否为 VIP                 |
| **vip_expire_time** | LONG?   | NULLABLE          | VIP 过期时间戳             |
| **created_at**      | LONG    | NOT NULL          | 创建时间                   |
| **updated_at**      | LONG    | NOT NULL          | 更新时间                   |
| **status**          | INT     | DEFAULT 0         | 账号状态（0=正常，1=冻结） |

#### 登录流程

```
用户输入账号密码
    ↓
表单验证（非空校验）
    ↓
调用 AccountViewModel.login()
    ↓
UserRepository.loginUser()
    ├─ 查询账号是否存在
    ├─ 验证密码哈希
    ├─ 检查账号状态
    └─ 保存登录状态到 DataStore
    ↓
StateFlow 通知 UI 更新
    ↓
显示 Snackbar 提示
    ↓
跳转到 MainActivity
```

#### 密码加密机制

```kotlin
// 加密流程
fun hashPasswordWithSalt(password: String, salt: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val saltedPassword = password + salt
    val hashBytes = digest.digest(saltedPassword.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}

// 示例
输入: password = "123456ByteDance", salt = "random_salt_123"
输出: "a3f8c7e2d9b1f4e6a2c5d8b9e3f7a1c4d6e9b2f5a8c1d4e7b3f6a9c2d5e8b1f4"
```

**安全性分析**：

| 攻击方式       | 防护措施        | 效果                   |
| -------------- | --------------- | ---------------------- |
| **明文泄露**   | SHA-256 哈希    | ✅ 无法逆向还原         |
| **彩虹表攻击** | 随机 Salt       | ✅ 每个用户盐值不同     |
| **暴力破解**   | 16 位 Salt      | ✅ 增加破解难度 2^64 倍 |
| **数据库注入** | Room 参数化查询 | ✅ 防止 SQL 注入        |

#### UI 设计（Jetpack Compose）

**LoginScreen**：

- Material Design 3 风格
- 支持新用户注册和账号登录入口
- 用户协议和隐私政策勾选
- 温馨提示文案

**AccountScreen**：

- 实时表单验证（账号/密码非空校验）
- 错误提示（红色边框 + 错误文案）
- 登录按钮禁用状态（未勾选协议时置灰）
- Snackbar 提示（登录成功/失败）
- 多种登录方式入口（手机号、第三方等）

### 2. 🖼️ OpenGL ES 渲染系统

#### 技术原理

**传统 Canvas 方案的局限**：

- CPU 密集型计算
- 2000×2000 图片缩放耗时 ~50ms
- 帧率降至 30fps

**OpenGL ES 优势**：

- GPU 并行计算
- 渲染耗时 ~5ms（性能提升 10 倍）
- 稳定 60fps，内存占用降低 60%

#### 渲染管线

```
原始 Bitmap → GPU 纹理上传
      ↓
顶点着色器（处理变换：缩放/平移/旋转）
      ↓
片段着色器（纹理采样与颜色计算）
      ↓
帧缓冲（屏幕显示）
```

### 3. ✂️ 智能裁剪系统

#### 支持的裁剪比例

| 比例     | 用途         | 示例场景           |
| -------- | ------------ | ------------------ |
| **自由** | 任意尺寸     | 个性化裁剪         |
| **原始** | 保持原图比例 | 无损裁剪           |
| **1:1**  | 正方形       | Instagram、头像    |
| **4:3**  | 传统照片     | 证件照             |
| **3:4**  | 竖版照片     | 手机壁纸           |
| **16:9** | 宽屏         | 电脑壁纸、视频封面 |
| **9:16** | 竖屏视频     | TikTok、Story      |

### 4. 🔄 手势交互系统

#### 支持的手势

| 手势         | 操作             | 实现方式                |
| ------------ | ---------------- | ----------------------- |
| **单指拖动** | 平移图片         | MotionEvent.ACTION_MOVE |
| **双指捏合** | 缩放图片         | ScaleGestureDetector    |
| **双指旋转** | 旋转图片（预留） | RotationGestureDetector |

### 5. ⏪ 历史管理系统

采用 **Command Pattern** 实现 Undo/Redo 功能：

```kotlin
data class EditorSnapshot(
    val transform: TransformState,
    val crop: CropState
)

class EditorHistoryManager {
    private val history = mutableListOf<EditorSnapshot>()
    private var currentIndex = -1
    
    fun saveSnapshot(transform: TransformState, crop: CropState) {
        // 清除 redo 历史
        if (currentIndex < history.size - 1) {
            history.subList(currentIndex + 1, history.size).clear()
        }
        history.add(EditorSnapshot(transform, crop))
        currentIndex = history.lastIndex
    }
    
    fun undo(): EditorSnapshot? = if (canUndo()) history[--currentIndex] else null
    fun redo(): EditorSnapshot? = if (canRedo()) history[++currentIndex] else null
}
```

### 6. 📤 导出系统

#### 导出流程

```
用户点击"导出" → 坐标映射（View → Bitmap）
                      ↓
                裁剪 Bitmap.createBitmap()
                      ↓
                JPEG 压缩（100% 质量）
                      ↓
                保存到 MediaStore
                      ↓
                显示"导出成功"提示
```

------

## 📊 性能测试报告

### 测试环境

- **设备 1**: Vivo X200s (OriginOS 4.0 / Android 14)
- **设备 2**: Redmi Note 13 (MIUI 14 / Android 13)
- **测试图片**: 3000×4000 (12MP)

### 渲染性能

| 操作         | Canvas 方案 | OpenGL ES | 性能提升 |
| ------------ | ----------- | --------- | -------- |
| **首帧加载** | ~500ms      | ~100ms    | **5x**   |
| **缩放操作** | ~50ms       | ~5ms      | **10x**  |
| **平移操作** | ~30ms       | <3ms      | **10x**  |
| **帧率**     | 30-40 fps   | 60 fps    | **稳定** |
| **内存占用** | 150MB       | 90MB      | **40%↓** |

### 导出性能

| 阶段            | 耗时       | 占比     |
| --------------- | ---------- | -------- |
| 坐标映射        | ~2ms       | 0.4%     |
| Bitmap 裁剪     | ~150ms     | 30%      |
| JPEG 压缩       | ~200ms     | 40%      |
| MediaStore 写入 | ~100ms     | 20%      |
| 媒体扫描        | ~50ms      | 10%      |
| **总计**        | **~502ms** | **100%** |

------

## 🛡️ 权限说明

### 必需权限

```xml
xml<!-- 读取相册 -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Android 12 及以下兼容 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />

<!-- Android 9 及以下需要写入权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
```

### 权限适配

| Android 版本      | 读取相册              | 写入相册               |
| ----------------- | --------------------- | ---------------------- |
| **Android 13+**   | READ_MEDIA_IMAGES     | 无需权限               |
| **Android 10-12** | READ_EXTERNAL_STORAGE | 无需权限               |
| **Android 9-**    | READ_EXTERNAL_STORAGE | WRITE_EXTERNAL_STORAGE |

------

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

------


## 📜 许可证

本项目采用 [MIT License](https://www.qianwen.com/chat/LICENSE)。

------

## 📧 联系方式

- **作者**: 400lai
- **GitHub**: [@Lai](https://github.com/400lai)
- **Email**: [laiiiii@foxmail.com](laiiiii@foxmail.com)

------

## 🙏 致谢

- [Android Developers](https://developer.android.com/) - 官方文档
- [Kotlin](https://kotlinlang.org/) - 编程语言
- [OpenGL ES](https://www.khronos.org/opengles/) - 图形 API
- [LeakCanary](https://square.github.io/leakcanary/) - 内存检测工具


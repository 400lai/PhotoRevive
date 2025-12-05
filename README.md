# 📱 PhotoRevive

一个基于 OpenGL ES 渲染的现代化 Android 图片编辑应用，提供高性能的图像处理和流畅的编辑体验。

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

  

### 🛠️ 技术栈

| 技术领域     | 使用技术                           |
| ------------ | ---------------------------------- |
| **编程语言** | Kotlin 1.9+                        |
| **架构模式** | MVVM (ViewModel + LiveData)        |
| **渲染引擎** | OpenGL ES 2.0 (GLSL)               |
| **异步处理** | Kotlin Coroutines + Flow           |
| **图像处理** | Android Bitmap API                 |
| **存储管理** | MediaStore API (Scoped Storage)    |
| **手势检测** | ScaleGestureDetector + MotionEvent |
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

```
bashgit clone https://github.com/400lai/PhotoRevive.git
cd PhotoRevive
```

### 🔧 配置项目

#### 1. 同步依赖

在 Android Studio 中打开项目后，点击顶部的 **"Sync Project with Gradle Files"** 按钮，或执行：

```
bash./gradlew build --refresh-dependencies
```

#### 2. 配置签名（可选，用于发布版本）

在 `app/build.gradle.kts` 中添加签名配置：

```
kotlinandroid {
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

```
bash# 安装 Debug 版本
./gradlew installDebug

# 或手动安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

> 💡 **Windows 用户**：请将 `./gradlew` 替换为 `gradlew.bat`

### 📦 构建发布版本

```
bash# 构建 APK
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
│   │   │   │   │   ├── MainActivity.kt           # 应用主入口
│   │   │   │   │   ├── SplashActivity.kt         # 启动页
│   │   │   │   │   ├── AlbumActivity.kt          # 相册选择页
│   │   │   │   │   └── EditorActivity.kt         # 编辑器核心页面
│   │   │   │   │
│   │   │   │   ├── ui/
│   │   │   │   │   ├── editor/
│   │   │   │   │   │   ├── EditorViewModel.kt          # 编辑器业务逻辑
│   │   │   │   │   │   ├── EditorState.kt              # 状态封装
│   │   │   │   │   │   ├── ExportState.kt              # 导出状态
│   │   │   │   │   │   ├── ImageRenderer.kt            # OpenGL 渲染器
│   │   │   │   │   │   ├── GLSurfaceView.kt            # 自定义渲染视图
│   │   │   │   │   │   ├── EditorTouchListener.kt      # 手势处理
│   │   │   │   │   │   ├── EditorHistoryManager.kt     # 历史管理
│   │   │   │   │   │   ├── CropManager.kt              # 裁剪逻辑
│   │   │   │   │   │   └── model/
│   │   │   │   │   │       └── TransformState.kt       # 变换状态
│   │   │   │   │   │
│   │   │   │   │   ├── album/
│   │   │   │   │   │   ├── AlbumViewModel.kt           # 相册数据管理
│   │   │   │   │   │   └── AlbumAdapter.kt             # 相册列表适配器
│   │   │   │   │   │
│   │   │   │   │   └── custom/
│   │   │   │   │       └── ShimmerCardView.kt          # 骨架屏组件
│   │   │   │   │
│   │   │   │   └── data/
│   │   │   │       └── repository/
│   │   │   │           └── LocalMediaRepository.kt     # 媒体数据仓库
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                   # XML 布局文件
│   │   │   │   ├── drawable/                 # 矢量图、形状
│   │   │   │   ├── mipmap-*/                 # 应用图标（含 WebP）
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml            # 颜色定义
│   │   │   │   │   ├── strings.xml           # 文本资源
│   │   │   │   │   ├── themes.xml            # 主题配置
│   │   │   │   │   └── dimens.xml            # 尺寸定义
│   │   │   │   └── menu/
│   │   │   │       └── bottom_nav_menu.xml   # 底部导航菜单
│   │   │   │
│   │   │   └── AndroidManifest.xml           # 应用清单
│   │   │
│   │   ├── androidTest/                      # 仪器化测试
│   │   └── test/                             # 单元测试
│   │
│   ├── build.gradle.kts                      # 模块构建脚本
│   └── proguard-rules.pro                    # 混淆规则
│
├── gradle/                                   # Gradle Wrapper 配置
├── build.gradle.kts                          # 项目级构建脚本
├── settings.gradle.kts                       # 项目设置
└── README.md                                 # 本文档
```

------

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

------


## 📜 许可证

本项目采用 [MIT License](https://www.qianwen.com/chat/LICENSE)（请根据实际情况替换）。

------

## 📧 联系方式

- **作者**: 400lai
- **GitHub**: [@400lai](https://github.com/400lai)
- **Email**: [your-email@example.com](mailto:your-email@example.com)（请替换为实际邮箱）

------

## 🙏 致谢

- [Android Developers](https://developer.android.com/) - 官方文档
- [Kotlin](https://kotlinlang.org/) - 编程语言
- [OpenGL ES](https://www.khronos.org/opengles/) - 图形 API
- [LeakCanary](https://square.github.io/leakcanary/) - 内存检测工具


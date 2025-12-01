plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.laiiiii.photorevive"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.laiiiii.photorevive"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true      // 启用 Compose 编译支持
        viewBinding = true
        dataBinding = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Compose BOM（平台依赖）
    implementation(platform(libs.androidx.compose.bom))

    // ✅ Compose 核心库（自动匹配 BOM 版本）
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose) // 用于 setContent
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel + Compose

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // ✅ 正确添加 Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Compose 工具支持
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation("androidx.compose.ui:ui-tooling")

}

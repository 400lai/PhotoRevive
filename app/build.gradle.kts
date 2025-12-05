import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")

    //  应用自定义插件
    alias(libs.plugins.photorevive.build.analysis)

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

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties().apply {
        if (keystorePropertiesFile.exists()) {
            load(FileInputStream(keystorePropertiesFile))
        }
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String? ?: ""
                keyPassword = keystoreProperties["keyPassword"] as String? ?: ""
                storeFile = file(keystoreProperties["storeFile"] as String? ?: "")
                storePassword = keystoreProperties["storePassword"] as String? ?: ""
            }
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release") // 👈 关键！启用签名
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
        compose = true
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

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose Core
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)          // ✅ 必需：LazyColumn, Image, Modifier 等
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose Navigation (if you use NavHost)
    implementation(libs.androidx.navigation.compose)         // ✅ 替换原来的 navigation-runtime-android

    // Lifecycle + Compose integration
    implementation(libs.androidx.lifecycle.runtime.compose)  // ✅ 推荐

    // ViewPager2 (View system, not Compose)
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Compose Tooling (Preview + Debug)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Room dependencies
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    kapt("androidx.room:room-compiler:2.7.0")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
}
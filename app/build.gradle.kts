plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.bloom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bloom"
        minSdk = 24
        targetSdk = 35
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
        compose = true
    }
}

dependencies {
    // ✅ Compose BOM 사용 (개별 버전 관리 X)
    implementation(platform(libs.androidx.compose.bom))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ Compose Core (UI & Material 3)
    implementation("androidx.compose.material3:material3:1.2.0") // Material3 최신버전
    implementation("androidx.compose.material:material-icons-extended:1.5.1") // 아이콘 라이브러리
    implementation("androidx.compose.foundation:foundation:1.5.1") // LazyColumn 포함
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1") // UI 미리보기 지원
    implementation("androidx.compose.runtime:runtime-livedata:1.5.1") // Runtime 지원
    implementation("androidx.navigation:navigation-compose:2.7.5") // Jetpack Navigation
    implementation("androidx.activity:activity-compose:1.8.2") // Activity Compose 지원

    // ✅ AndroidX Core (필수 라이브러리)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.preference.ktx)

    // ✅ 테스트 의존성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // ✅ 디버깅 도구 (빌드 오류 검출 & 미리보기)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


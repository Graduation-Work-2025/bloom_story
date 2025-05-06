package com.example.bloom.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ✅ 네가 원하는 초록 계열 색상
private val BloomPrimary = Color(0xFF55996F)
private val BloomSecondary = Color(0xFF82B69B) // 보조 초록색
private val BloomTertiary = Color(0xFFCDEADF)  // 연한 포인트 색

private val DarkColorScheme = darkColorScheme(
    primary = BloomPrimary,
    secondary = BloomSecondary,
    tertiary = BloomTertiary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BloomPrimary,
    secondary = BloomSecondary,
    tertiary = BloomTertiary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

@Composable
fun BloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ⚠️ 강제 끔 (커스텀 컬러가 우선)
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

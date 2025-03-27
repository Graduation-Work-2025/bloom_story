package com.example.bloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bloom.navigation.AppNavigator
import com.example.bloom.ui.theme.BloomTheme
import com.example.bloom.util.PreferenceManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PreferenceManager.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            BloomTheme {
                AppNavigator()
            }
        }
    }
}

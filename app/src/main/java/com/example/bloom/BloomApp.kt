package com.example.bloom

import android.app.Application
import com.example.bloom.util.PreferenceManager

class BloomApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(applicationContext)
    }
}

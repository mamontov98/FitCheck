package com.example.fitcheck.utilities

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
    }
}

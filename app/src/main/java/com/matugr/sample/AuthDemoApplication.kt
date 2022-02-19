package com.matugr.sample

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AuthDemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
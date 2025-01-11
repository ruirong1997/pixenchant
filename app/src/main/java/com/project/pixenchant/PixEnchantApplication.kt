package com.project.pixenchant

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PixEnchantApplication: Application() {

    companion object {

        private lateinit var sAppContext: Context

        fun getContext(): Context {
            return sAppContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sAppContext = applicationContext
    }

}
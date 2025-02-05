package com.project.pixenchant

import android.app.Application
import android.content.Context
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
//import org.opencv.android.OpenCVLoader

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

//        if (OpenCVLoader.initLocal()) {
//            Log.d("OpenCVLoader", "Opencv加载成功。。。")
//        } else {
//            Log.d("OpenCVLoader", "Opencv加载失败。。。")
//        }
    }

}
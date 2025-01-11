package com.project.pixenchant.utils

import android.annotation.SuppressLint
import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

@SuppressLint("WrongConstant")
object SystemUiUtils {

    /**
     * 隐藏系统状态栏和导航栏，提供一个封装的方法避免重复使用警告常量。
     */
    fun hideSystemBars(activity: Activity) {
        // 通过 WindowInsetsController 控制隐藏状态栏和导航栏
        val insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        insetsController.hide(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE)
    }

    /**
     * 恢复系统状态栏和导航栏显示
     */
    fun showSystemBars(activity: Activity) {
        val insetsController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        insetsController.show(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE)
    }
}

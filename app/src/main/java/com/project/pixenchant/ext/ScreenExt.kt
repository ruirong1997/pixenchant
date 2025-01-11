package com.project.pixenchant.ext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

// 获取屏幕的宽度和高度
fun Context.getScreenWidth(): Int {
    val metrics = resources.displayMetrics
    return metrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    val metrics = resources.displayMetrics
    return metrics.heightPixels
}

// 屏幕中心X位置
@Composable
fun getScreenCenterXPx(): Int {
    val context = LocalContext.current
    return context.getScreenWidth() / 2
}

// 屏幕中心X位置
@Composable
fun getScreenCenterXDp(): Int {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    return ((context.getScreenWidth() / 2) / density).roundToInt()
}

// 屏幕中心Y位置
@Composable
fun getScreenCenterYPx(): Int {
    val context = LocalContext.current
    return context.getScreenHeight() / 2
}

// 屏幕中心Y位置
@Composable
fun getScreenCenterYDp(): Int {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    return ((context.getScreenHeight() / 2) / density).roundToInt()
}

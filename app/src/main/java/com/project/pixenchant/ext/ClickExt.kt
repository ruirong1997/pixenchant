package com.project.pixenchant.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun Modifier.singleClick(
    interval: Long = 500L, // 默认点击间隔
    onClick: () -> Unit
): Modifier = pointerInput(Unit) {
    var lastClickTime = 0L
    coroutineScope {
        detectTapGestures {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= interval) {
                lastClickTime = currentTime
                launch {
                    onClick()
                }
            }
        }
    }
}

fun Modifier.noRippleSingleClick(
    interval: Long = 500L, // 默认点击间隔
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime = remember { 0L } // 防止快速点击
    clickable(
        indication = null, // 禁用涟漪
        interactionSource = remember { MutableInteractionSource() }
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > interval) { // 500ms 防抖间隔
            lastClickTime = currentTime
            onClick()
        }
    }
}


//
//fun (() -> Unit).singleClick(interval: Long = 500L): () -> Unit {
//    var lastClickTime = 0L
//    return {
//        val currentTime = System.currentTimeMillis()
//        if (currentTime - lastClickTime >= interval) {
//            lastClickTime = currentTime
//            this()
//        }
//    }
//}
// 扩展函数：防止多次点击
fun <T> (() -> T).singleClick(interval: Long = 500L): () -> Unit {
    var lastClickTime = 0L
    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            this()
        }
    }
}


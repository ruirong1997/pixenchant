package com.project.pixenchant.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class LateInitState<T> {
    private var _value: T? = null
    val isInitialized: Boolean get() = _value != null
    var value: T
        get() = _value ?: error("Value is not initialized yet!")
        set(value) {
            _value = value
        }
}

@Composable
fun <T> rememberLateInitState(): LateInitState<T> {
    return remember { LateInitState<T>() }
}
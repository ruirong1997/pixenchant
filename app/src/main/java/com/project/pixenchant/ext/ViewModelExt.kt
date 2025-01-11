package com.project.pixenchant.ext

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.pixenchant.PixEnchantApplication

inline fun <VM : ViewModel> viewModelFactory(crossinline factory: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return factory() as T
        }
    }
}

// 为 ViewModel 创建扩展函数
fun ViewModel.getAppContext(): Context {
    return PixEnchantApplication.getContext()
}

fun getAppContext(): Context {
    return PixEnchantApplication.getContext()
}
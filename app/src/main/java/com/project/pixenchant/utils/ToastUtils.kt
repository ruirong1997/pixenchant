package com.project.pixenchant.utils

import android.content.Context
import android.widget.Toast
import com.project.pixenchant.ext.getAppContext

object ToastUtils {

    private var currentToast: Toast? = null

    // 显示短时间 Toast
    fun showShort(message: String) {
        // 取消当前的 Toast，如果有的话
        currentToast?.cancel()

        // 显示新的 Toast
        currentToast = Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    // 显示长时间 Toast
    fun showLong(message: String) {
        // 取消当前的 Toast，如果有的话
        currentToast?.cancel()

        // 显示新的 Toast
        currentToast = Toast.makeText(getAppContext(), message, Toast.LENGTH_LONG)
        currentToast?.show()
    }

    // 显示短时间 Toast, 通过资源 ID
    fun showShort(resId: Int) {
        // 取消当前的 Toast，如果有的话
        currentToast?.cancel()

        // 显示新的 Toast
        currentToast = Toast.makeText(getAppContext(), resId, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    // 显示长时间 Toast, 通过资源 ID
    fun showLong( resId: Int) {
        // 取消当前的 Toast，如果有的话
        currentToast?.cancel()

        // 显示新的 Toast
        currentToast = Toast.makeText(getAppContext(), resId, Toast.LENGTH_LONG)
        currentToast?.show()
    }
}

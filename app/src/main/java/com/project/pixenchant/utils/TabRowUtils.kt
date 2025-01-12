package com.project.pixenchant.utils

object TabRowUtils {

    private fun hackTabMinWidth() {
        try {
            val clazz = Class.forName("androidx.compose.material3.TabRowKt")
            val field = clazz.getDeclaredField("ScrollableTabRowMinimumTabWidth")
            field.isAccessible = true
            field.setFloat(null, 0.0f) // 将最小宽度设置为 0
        } catch (e: Exception) {
            e.printStackTrace() // 打印错误日志，便于调试
        }
    }

    fun initTabMinWidthHacking() = runCatching {
        hackTabMinWidth()
    }
}
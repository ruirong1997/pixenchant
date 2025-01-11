package com.project.pixenchant.ext

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

// 将 Dp 转换为 px
fun Dp.toPx(density: Density): Float {
    return with(density) { this@toPx.toPx() }
}
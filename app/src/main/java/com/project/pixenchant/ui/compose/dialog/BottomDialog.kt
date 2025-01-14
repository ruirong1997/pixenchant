package com.project.pixenchant.ui.compose.dialog

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.pixenchant.ext.noRippleSingleClick

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun BottomDialog(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    val offsetY = remember { Animatable(500f) }

    // 动画：滑动效果
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(durationMillis = 500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // 半透明背景
            .clickable {  }.noRippleSingleClick { onDismissRequest() } // 监听外部区域点击
    ) {
        Box(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .offset(y = Dp(offsetY.value))
                .clickable(enabled = false) {} // 阻止内部点击触发外部点击事件
        ) {
            content()
        }
    }
}
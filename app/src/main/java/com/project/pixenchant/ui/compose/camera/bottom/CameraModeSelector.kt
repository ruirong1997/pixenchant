package com.project.pixenchant.ui.compose.camera.bottom

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.CameraMode
import com.project.pixenchant.camera2.data.CameraModeItem
import com.project.pixenchant.ext.getScreenCenterXPx
import com.project.pixenchant.ext.noRippleSingleClick
import com.project.pixenchant.viewmodel.Camera2ViewModel
import com.project.pixenchant.ext.getScreenCenterXDp
import kotlin.math.roundToInt


/**
 * 模式选择
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun CameraModeSelector(viewModel: Camera2ViewModel) {

    val cameraModeList = viewModel.getCameraModeList()
    val selectedMode by viewModel.cameraMode.collectAsState(CameraMode.PHOTO)

    val itemHeight = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.item_min_height).toDp()
    }

    // 获取屏幕宽度的中间位置
    val screenCenterX = getScreenCenterXPx()
    val screenCenterXDp = getScreenCenterXDp()
    val density = LocalDensity.current.density

    // 记录选中控件的宽度和位置
    var selectedButtonWidth by remember { mutableIntStateOf(0) }
    var selectedButtonPosition by remember { mutableIntStateOf(0) }

    // 记录Row的宽度和位置
    var rowPosition by remember { mutableIntStateOf(0) }

    val durationTime = 300

    // 动画位置变化
    var targetOffset by remember { mutableIntStateOf(screenCenterXDp / 2) }
    val animatedRowOffset = animateDpAsState(
        targetValue = targetOffset.dp, // 宽度动画：点击后偏移
        animationSpec = tween(durationMillis = durationTime), label = "" // 动画持续时间
    )

    // 动画宽度变化
    var targetWidth by remember { mutableIntStateOf(0) }
    val animatedWidth = animateDpAsState(
        targetValue = targetWidth.dp, // 宽度动画：点击后变大
        animationSpec = tween(durationMillis = durationTime), label = "" // 动画持续时间
    )

    /**
     * 计算选中后的UI变化
     */
    fun computeSelectedUi(widthPx: Int, positionPx: Int) {
        selectedButtonWidth = widthPx
        selectedButtonPosition = positionPx
        targetOffset =
            calculateOffset(rowPosition, screenCenterX, selectedButtonPosition, widthPx, density)
        targetWidth = (widthPx / density).roundToInt()
    }

    Box {
        //背景
        Box(modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = ((screenCenterXDp - (targetWidth / 2)).dp)) //居中位置
            .width(animatedWidth.value) // 动态宽度
            .height(itemHeight)
            .clip(RoundedCornerShape(itemHeight / 2)) // 使用圆角
            .background(Color.White)
        )

        //选项
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = animatedRowOffset.value) // 使用动画偏移
                .onGloballyPositioned { coordinates ->
                    rowPosition = coordinates.boundsInWindow().left.toInt()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            cameraModeList.forEach { data ->
                val mode = data.mode
                var widthPx = 0
                var positionPx = 0
                TextButton(
                    itemHeight = itemHeight,
                    data = data,
                    selectedMode = selectedMode,
                    onClick = {
                        if (selectedMode != mode) {
                            viewModel.setCameraMode(mode)
                            computeSelectedUi(widthPx, positionPx)
                        }
                    },
                    onPositioned = { coordinates ->
                        widthPx = coordinates.size.width
                        positionPx = coordinates.boundsInWindow().left.toInt()
                        if (selectedMode == mode) {
                            computeSelectedUi(widthPx, positionPx)
                        }
                    }
                )
            }
        }

    }
}

// 计算偏移量的函数封装
private fun calculateOffset(
    rowPosition: Int,
    screenCenterX: Int,
    selectedButtonPosition: Int,
    selectedButtonWidth: Int,
    density: Float
): Int {
    return ((rowPosition + screenCenterX - (selectedButtonPosition + (selectedButtonWidth / 2))) / density).toInt()
}

@Composable
fun TextButton(
    itemHeight: Dp,
    data: CameraModeItem,
    selectedMode: CameraMode,
    onClick: () -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
) {
    val mode = data.mode
    val title = data.name

    Text(
        text = title,
        modifier = Modifier
            .height(itemHeight)
            .clickable {
            }.noRippleSingleClick {
                onClick()
            }
            .onGloballyPositioned { coordinates ->
                onPositioned(coordinates)
            }
            .padding(start = 8.dp, end = 8.dp)
            .wrapContentSize(Alignment.Center), // 使文字居中, // 设置 padding
        style = TextStyle(
            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal,
            color = if (selectedMode == mode) Color.Black else Color.White
        )
    )
}

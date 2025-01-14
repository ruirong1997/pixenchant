package com.project.pixenchant.ui.view

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.pixenchant.R
import com.project.pixenchant.ext.dpToPx
import com.project.pixenchant.ext.pxToDp

@Composable
fun HorizontalProgressBar(
    modifier: Modifier = Modifier,
    progress: Float = 0f, // 外部传入进度
    normalProgress: Float = 50f,
    onProgressChanged: (Float) -> Unit, // 外部回调更新进度
    minProgress: Float = 0f, // 最小进度
    maxProgress: Float = 1f, // 最大进度
    barHeight: Dp = 6.dp, // 进度条高度
    backgroundColor: Color = colorResource(id = R.color.progress_background_color), // 背景颜色
    foregroundColor: Color = colorResource(id = R.color.progress_foreground_color), // 前景颜色
) {
    var mCurrentProgress by remember { mutableFloatStateOf(progress) }
    val mNormalProgress by remember { mutableFloatStateOf(normalProgress / 100) }

    val mCornerRadius by remember { mutableStateOf((barHeight / 2)) }

    // 创建Paint对象
    val mPaint = Paint().apply {
        color = Color.White.toArgb()
        textSize = 12.0f.dpToPx
    }

    //静态圆
    val mNormalCircleRadius = 4
    var mNormalCircleCenterX = 0.0f // 圆心的 x 坐标在进度条的右边
    var mNormalCircleCenterY = 0.0f // 圆心的 y 坐标在进度条的中间

    //进度条 动态圆
    val mProgressCircleRadius = 12 // 圆的半径与进度条高度相等，或者你可以设置一个自定义的圆半径
    var mProgressCircleCenterX = 0.0f // 圆心的 x 坐标在进度条的右边
    var mProgressCircleCenterY = 0.0f // 圆心的 y 坐标在进度条的中间

    var mHeight = 0f
    var containerWidth by remember { mutableStateOf(0.dp) }


    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width.pxToDp.dp
            }
    ) {
        Canvas(modifier = Modifier
            .width(containerWidth)
            .height(barHeight * 2)
            .align(Alignment.Center)
            .pointerInput(Unit) {
                detectTapGestures(
                    // 检测按下类型的手势
                    onPress = {
                        val newProgress =
                            (it.x / containerWidth.toPx()).coerceIn(minProgress, maxProgress)
                        if (newProgress != mCurrentProgress) {
                            mCurrentProgress = newProgress
                            onProgressChanged(mCurrentProgress) // 更新进度
                        }
                    },
                )
            }
            .pointerInput(Unit) {// 手势输入处理
                // 处理滑动手势
                detectHorizontalDragGestures { _, dragAmount ->
                    val newProgress =
                        (mCurrentProgress + dragAmount / containerWidth.toPx()).coerceIn(
                            minProgress,
                            maxProgress
                        )
                    if (newProgress != mCurrentProgress) {
                        mCurrentProgress = newProgress
                        onProgressChanged(mCurrentProgress) // 更新进度
                    }
                }
            }
        ) {

            mHeight = size.height / 2
            mNormalCircleCenterX = containerWidth.toPx() * mNormalProgress // 圆心的 x 坐标在进度条的右边
            mNormalCircleCenterY = mHeight // 圆心的 y 坐标在进度条的中间

            mProgressCircleCenterX = containerWidth.toPx() * mCurrentProgress // 圆心的 x 坐标在进度条的右边
            mProgressCircleCenterY = mHeight // 圆心的 y 坐标在进度条的中间

            // 背景进度条
            drawRoundRect(
                color = backgroundColor, // 设置银色，透明度为30%
                size = Size(containerWidth.toPx(), mHeight),
                topLeft = Offset(0f, size.height / 4), // 垂直居中
                cornerRadius = CornerRadius(mCornerRadius.toPx()) // 设置圆角
            )

            // 前景进度条
            drawRoundRect(
                color = foregroundColor,
                size = Size(containerWidth.toPx() * mCurrentProgress, mHeight),
                topLeft = Offset(0f, size.height / 4), // 垂直居中
                cornerRadius = CornerRadius(mCornerRadius.toPx())
            )

            // 静态圆
            drawCircle(
                color = Color.White,
                radius = mNormalCircleRadius.dpToPx,
                center = Offset(mNormalCircleCenterX, mNormalCircleCenterY)
            )

            // 动态圆
            drawCircle(
                color = Color.White,
                radius = mProgressCircleRadius.dpToPx,
                center = Offset(mProgressCircleCenterX, mProgressCircleCenterY)
            )

            // 绘制文本，位置在圆形的上方
            drawContext.canvas.nativeCanvas.drawText(
                "${(mCurrentProgress * 100).toInt()}%",
                mProgressCircleCenterX - (mPaint.measureText("${(mCurrentProgress * 100).toInt()}%") / 2), // x坐标
                mProgressCircleCenterY - mProgressCircleRadius - 20f.dpToPx,  // y坐标，圆形上方，文本的高度稍微调整
                mPaint
            )
        }
    }


}
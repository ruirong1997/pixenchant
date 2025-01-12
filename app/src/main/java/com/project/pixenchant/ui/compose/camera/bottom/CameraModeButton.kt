package com.project.pixenchant.ui.compose.camera.bottom

import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.CameraMode
import com.project.pixenchant.viewmodel.Camera2ViewModel
import com.project.pixenchant.ui.compose.camera.CaptureImageDialog

@Composable
fun CameraModeButton(viewModel: Camera2ViewModel, modifier: Modifier) {

    val cameraMode by viewModel.cameraMode.collectAsState(CameraMode.PHOTO)

    val btnRadius = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.btn_radius).toDp()
    }

    val space = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.space_size).toDp()
    }

    // 获取Context
    val context = LocalContext.current
    // 获取WindowManager
    val windowManager = context.getSystemService(WindowManager::class.java)

    // 用于存储拍摄的图片
    val capturedImage = remember { mutableStateOf<Bitmap?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    // 显示拍摄的图片对话框

    Box(
        modifier = modifier
            .size(btnRadius)
            .clickable {
                viewModel.capturePhoto{ bitmap ->
                    capturedImage.value = bitmap
                    isVisible = true
                }
            }, // Box的大小
        contentAlignment = Alignment.Center // Box中的内容居中
    ) {
        if (isVisible) {
            CaptureImageDialog(
                bitmap = capturedImage.value,
                onDismiss = {
                    isVisible = false
                    capturedImage.value = null }
            )
        }

        Canvas(modifier = Modifier.size(btnRadius)) {
            // 在Canvas中绘制圆形
            drawCircle(color = getInnerColor(cameraMode), radius = btnRadius.value)

            drawCircle(
                color = Color.White,
                radius = btnRadius.value + space.value,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f) // 设置圆环的宽度
            )
        }

        if (cameraMode == CameraMode.SEGMENTED_VIDEO) {
            Image(
                painter = painterResource(id = R.drawable.ic_segment),
                contentDescription = "",
                modifier = Modifier.size(36.dp)
            )
        }
    }

}

private fun getInnerColor(cameraMode: CameraMode): Color {
    return when (cameraMode) {
        CameraMode.PHOTO -> Color.White
        CameraMode.VIDEO, CameraMode.SEGMENTED_VIDEO -> Color.Red
        else -> Color.White
    }
}

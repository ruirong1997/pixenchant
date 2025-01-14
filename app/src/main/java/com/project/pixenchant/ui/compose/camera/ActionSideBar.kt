package com.project.pixenchant.ui.compose.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.pixenchant.R
import com.project.pixenchant.ext.noRippleSingleClick
import com.project.pixenchant.ui.compose.dialog.FaceDialog
import com.project.pixenchant.viewmodel.Camera2ViewModel
import com.project.pixenchant.ui.compose.dialog.FilterDialog
import com.project.pixenchant.ui.compose.dialog.FilterSelectDialog
import com.project.pixenchant.viewmodel.DialogViewModel

/**
 * 右侧工具栏
 */
@Composable
fun ActionSideBar(cameraViewModel: Camera2ViewModel,
                  dialogViewModel: DialogViewModel,
                  modifier: Modifier = Modifier) {

    val openFilterDialog = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp) // 设置所有项之间的间隔
    ) {

        ActionItem(R.drawable.ic_switch_camera, "翻转") { cameraViewModel.switchCamera() }
        ActionItem(R.drawable.ic_filter, "滤镜") { dialogViewModel.showFilterDialog(true) }
        ActionItem(R.drawable.ic_face_enhancement, "美颜") { dialogViewModel.showFaceDialog(true) }
//        ActionItem(R.drawable.ic_switch_camera, "切换摄像头") { cameraViewModel.switchCamera() }

    }
}

@Composable
fun ActionItem(resource: Int, text: String, onClick: () -> Unit) {

    val iconSize = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.icon_size).toDp()
    }

    // 使用 Column 来垂直排列图片和文字
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // 图片和文字居中
        modifier = Modifier
            .noRippleSingleClick {
                onClick.invoke()
            }
    ) {
        Image(
            painter = painterResource(id = resource),
            contentDescription = text,
            modifier = Modifier.size(iconSize)
        )

        // 显示文字
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium, // 可以调整字体样式
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp) // 图片与文字之间的间距
        )
    }
}




@Composable
fun MyPopupDemo() {
    var openDialog by remember { mutableStateOf(false) }

    val durationTime = 300 // 动画时长
    val onDismiss: () -> Unit = {
        // 外部回调，关闭时执行的操作
        println("Popup dismissed")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Button(onClick = { openDialog = true }) {
            Text("Open Dialog")
        }
    }
    // 点击按钮显示 Popup


    // Popup 显示内容
    Popup(
        onDismissRequest = {
            openDialog = false
            onDismiss() // 关闭时调用外部回调
        }
    ) {
        // 使用 AnimatedVisibility 来为 Popup 添加动画效果
        AnimatedVisibility(
            visible = openDialog,
            enter = slideInVertically(
                initialOffsetY = { it }, // 从底部弹出
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeIn(animationSpec = tween(durationMillis = durationTime)), // 渐现效果
            exit = slideOutVertically(
                targetOffsetY = { it }, // 向底部滑动消失
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeOut(animationSpec = tween(durationMillis = durationTime)) // 渐隐效果
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent) // 设置透明背景
                    .clickable {

                    }
                    .noRippleSingleClick {
                        openDialog = false // 点击外部关闭对话框
                        onDismiss() // 调用外部回调
                    }
                    .wrapContentSize(Alignment.BottomCenter) // 从底部弹出
            ) {
                // Dialog 内容
                FilterSelectDialog()
            }
        }
    }
}
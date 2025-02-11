package com.project.pixenchant.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.pixenchant.ui.compose.dialog.FaceDialog
import com.project.pixenchant.ui.compose.dialog.FilterDialog
import com.project.pixenchant.ui.compose.permission.CameraPermissionScreen
import com.project.pixenchant.viewmodel.DialogViewModel


@Composable
fun MainScreen(dialogViewModel: DialogViewModel = hiltViewModel()) {

    // 存储滑块的值
    val blurStrength = remember { mutableStateOf(0f) }


    val mIsOpenFilterDialog by dialogViewModel.showFilterDialog.collectAsState(false)
    val mIsOpenFaceDialog by dialogViewModel.showFaceDialog.collectAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CameraPermissionScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
        )

        // 底部控件
        BottomSelectorControl(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter) // 将底部控件对齐到底部
                .padding(16.dp) // 可选，设置容器的内边距
                .background(Color.Black)
        )


        //滤镜弹窗
        FilterDialog(
            openDialog = mIsOpenFilterDialog,
            onDismiss = { dialogViewModel.showFilterDialog(false) },
        )

        //美颜弹窗
        FaceDialog(
            openDialog = mIsOpenFaceDialog,
            onDismiss = { dialogViewModel.showFaceDialog(false) },
        )
        //测试用
        // 使用 AndroidView 来嵌入 GLSurfaceView
//        Surface(modifier = Modifier
//            .width(500.dp)
//            .height(500.dp), color = MaterialTheme.colorScheme.background) {
//            AndroidView(
//                factory = { context ->
//                    glSurfaceView = MyGLSurfaceView(context) // 创建并返回 MyGLSurfaceView
//                    glSurfaceView!!
//               },
//                modifier = Modifier.fillMaxSize()
//            )
//        }

//        // 控制模糊强度的滑块
//        BlurControlSlider { newStrength ->
////            glSurfaceView?.renderer?.setBlurRadius(newStrength)
//        }
//
//        BlurControlSlider { newStrength ->
////            glSurfaceView?.renderer?.setSigma(newStrength.toDouble())
//        }
    }
}

@Composable
fun BottomSelectorControl(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "相机",
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.Black),
            color = Color.White, // 设置文本颜色
            style = TextStyle(
                fontSize = 18.sp,// 设置字体大小
                fontWeight = FontWeight.Bold
            ) // 设置字体加粗
        )
    }
}

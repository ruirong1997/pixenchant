package com.project.pixenchant.ui.compose

import MyGLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.project.pixenchant.ui.compose.permission.CameraPermissionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun MainScreen() {

    // 存储滑块的值
    val blurStrength = remember { mutableStateOf(0f) }

    var glSurfaceView: MyGLSurfaceView? =null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
//        CameraPermissionScreen()

        // 使用 AndroidView 来嵌入 GLSurfaceView
        Surface(modifier = Modifier
            .width(500.dp)
            .height(500.dp), color = MaterialTheme.colorScheme.background) {
            AndroidView(
                factory = { context ->
                    glSurfaceView = MyGLSurfaceView(context) // 创建并返回 MyGLSurfaceView
                    glSurfaceView!!
               },
                modifier = Modifier.fillMaxSize()

            )
        }

        // 控制模糊强度的滑块
        BlurControlSlider { newStrength ->
//            glSurfaceView?.renderer?.setBlurRadius(newStrength)
        }

        BlurControlSlider { newStrength ->
//            glSurfaceView?.renderer?.setSigma(newStrength.toDouble())
        }
    }
}


@Composable
fun BlurControlSlider(onSliderValueChange: (Float) -> Unit) {
    var sliderPosition = remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .height(96.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Blur Strength: ${sliderPosition.value}", color = Color.Black)
        Slider(
            value = sliderPosition.value,
            onValueChange = {
                sliderPosition.value = it
                onSliderValueChange(it) // 传递新的模糊强度值
            },
            valueRange = 0f..50f, // 控制模糊强度的范围
            steps = 49
        )
    }
}
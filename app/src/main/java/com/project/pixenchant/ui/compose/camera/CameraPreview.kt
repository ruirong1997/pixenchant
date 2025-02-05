package com.project.pixenchant.ui.compose.camera

import android.app.Activity
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.project.pixenchant.viewmodel.Camera2ViewModel

// 封装相机预览逻辑
@Composable
fun CameraPreview(
    onInitialized: (SurfaceTexture) -> Unit,
    cameraViewModel: Camera2ViewModel,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val window = (context as? Activity)?.window

    // 设置状态栏为透明
    SideEffect {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            it.statusBarColor = Color.Transparent.toArgb()
            WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightStatusBars = false // 状态栏图标颜色
        }
    }

//    AndroidView(
//        factory = { context ->
//            TextureView(context).apply {
//                onInitialized(this) // 初始化回调
//            }
//        },
//        modifier = modifier
//    )

    AndroidView(
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3) // OpenGL ES 3.0

                val renderer = cameraViewModel.getRenderer()
                setRenderer(renderer) // 设置自定义渲染器

                renderer.mOnSurfaceTextureAvailable = { surfaceTexture ->
                    onInitialized(surfaceTexture)
                }
            }
        },
        modifier = modifier
    )
}

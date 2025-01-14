package com.project.pixenchant.ui.compose.camera

import android.graphics.SurfaceTexture
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.project.pixenchant.ui.compose.camera.bottom.BottomCameraControls
import com.project.pixenchant.viewmodel.Camera2ViewModel
import com.project.pixenchant.viewmodel.DialogViewModel


@Composable
fun Camera2Screen(
    camera2ViewModel: Camera2ViewModel = hiltViewModel(),
    dialogViewModel: DialogViewModel = hiltViewModel(),
) {
    val mSurfaceTexture = remember { mutableStateOf<SurfaceTexture?>(null) }

    val mIsShowBottomControl by dialogViewModel.showBottomControlDialog.collectAsState(true)
    val mIsShowSideBar by dialogViewModel.showSidebarDialog.collectAsState(true)

    val mLifecycleOwner = LocalLifecycleOwner.current
    val mLifecycle = mLifecycleOwner.lifecycle

    // 使用 remember 来保持对 observer 的引用
    val mObserver = remember {
        LifecycleEventObserver { _, event ->
            mSurfaceTexture.value?.apply {
                handleLifecycleEvent(event, camera2ViewModel)
            }
        }
    }

    LaunchedEffect(mLifecycle) {
        // 添加生命周期观察者
        mLifecycle.addObserver(mObserver)
    }

    DisposableEffect(mLifecycle) {
        // 清理观察者，避免内存泄漏
        onDispose {
            mLifecycle.removeObserver(mObserver)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CameraPreview 用于显示 TextureView 并初始化
        CameraPreview(
            onInitialized = { surfaceView ->
                Log.d("Camera2Screen", "CameraPreview onInitialized")
                mSurfaceTexture.value = surfaceView // 保存初始化后的 TextureView
                camera2ViewModel.setSurfaceView(surfaceView)
                camera2ViewModel.openCamera()
            },
            cameraViewModel = camera2ViewModel,
            modifier = Modifier.fillMaxSize()
        )


        mSurfaceTexture.value?.let { textureView ->
            // 右侧工具栏
            if (mIsShowSideBar) {
                ActionSideBar(
                    camera2ViewModel,
                    dialogViewModel,
                    Modifier.align(Alignment.TopEnd).padding(16.dp)
                )
            }
            if (mIsShowBottomControl) {
                //底部控制栏
                BottomCameraControls(
                    camera2ViewModel, Modifier
                        .align(Alignment.BottomCenter) // 将控制栏固定在屏幕底部
                )
            }
        }
    }
}

private fun handleLifecycleEvent(event: Lifecycle.Event, cameraViewModel: Camera2ViewModel) {
    when (event) {
        Lifecycle.Event.ON_RESUME -> {
            Log.d("Camera2Screen", "ON_RESUME")
            cameraViewModel.openCamera()
        }

        Lifecycle.Event.ON_PAUSE -> {
            Log.d("Camera2Screen", "ON_PAUSE")
            // 在 ON_PAUSE 中可以执行释放资源的操作
        }

        else -> {
            // 处理其他生命周期事件
        }
    }
}

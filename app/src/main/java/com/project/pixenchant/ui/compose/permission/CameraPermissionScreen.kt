package com.project.pixenchant.ui.compose.permission

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.pixenchant.ui.compose.camera.Camera2Screen
import com.project.pixenchant.viewmodel.Camera2ViewModel


@Composable
fun CameraPermissionScreen(
    modifier: Modifier,
    cameraViewModel: Camera2ViewModel = hiltViewModel()
) {
    // 动态监听权限状态
    val hasPermission by cameraViewModel.hasCameraPermission.collectAsState()

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraViewModel.updatePermissionStatus(isGranted)
        }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            // 检查权限并请求
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // UI 根据权限状态更新
    if (hasPermission) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 显示相机预览
            Camera2Screen()
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "需要摄像头权限才能继续", color = Color.Red)
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(text = "请求权限")
            }
        }
    }
}

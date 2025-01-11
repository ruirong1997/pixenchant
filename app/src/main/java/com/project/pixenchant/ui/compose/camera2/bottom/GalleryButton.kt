package com.project.pixenchant.ui.compose.camera2.bottom

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.project.pixenchant.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.pixenchant.camera2.viewmodel.Camera2ViewModel
import com.project.pixenchant.ui.compose.camera2.MediaScreen


@Composable
fun PhotoGalleryButton(modifier: Modifier = Modifier, camera2ViewModel: Camera2ViewModel = hiltViewModel()) {
    val context = LocalContext.current

    var isShowGallery by remember { mutableStateOf(false) }

    // 启动图库选择器的回调
    val openGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // 这里处理返回的URI，可以显示或播放视频
                Log.d("PHOTO_GALLERY", "Selected media URI: $uri")
            }
        }
    )

    // 权限请求启动器
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Log.d("PHOTO_GALLERY", "Permission granted: $isGranted")
            if (isGranted) {
                openGallery.launch("video/*") // 如果有权限，启动图库选择器
            }
        }
    )

    // 权限检查
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // 点击按钮时的行为
    IconButton(
        onClick = {
            if (hasPermission) {
                camera2ViewModel.stopPreview()
                camera2ViewModel.openMediaStorage()
                isShowGallery = true
            } else {
                // 如果没有权限，请求权限
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        },
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_switch_camera),
            contentDescription = "Gallery Button",
        )

        MediaScreen(
            isVisibleExternally = isShowGallery,
            onDismiss = {
                Log.d("Camera2ViewModel", "onDismiss = 1111")
                isShowGallery = false
                camera2ViewModel.closeMediaStorage()
                camera2ViewModel.startPreview()
            }
        )
    }
}


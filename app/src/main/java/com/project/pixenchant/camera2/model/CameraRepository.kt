package com.project.pixenchant.camera2.model

import android.app.Activity
import android.content.Context
import com.project.pixenchant.camera2.data.CameraModeItem
import com.project.pixenchant.camera2.data.CameraMode
import com.project.pixenchant.camera2.manager.CameraManager
import com.project.pixenchant.utils.PermissionUtil
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CameraRepository @Inject constructor(
    private val cameraManager: CameraManager
) {
    /**
     * 拍摄模式
     */
    val cameraModeList = listOf(
        CameraModeItem(CameraMode.SEGMENTED_VIDEO, "分段拍"),
        CameraModeItem(CameraMode.PHOTO, "拍照"),
        CameraModeItem(CameraMode.VIDEO, "视频")
    )



    /**
     * 当前使用的模式
     */
    private val _curCameraMode = MutableStateFlow(CameraMode.PHOTO)
    val curCameraMode = _curCameraMode



    fun setCameraMode(mode: CameraMode) {
        _curCameraMode.value = mode
    }

    // 检查相机权限
    fun checkPermission(context: Context): Boolean {
        return PermissionUtil.checkCameraPermission(context)
    }

    // 请求相机权限
    fun requestPermission(activity: Activity) {
        PermissionUtil.requestCameraPermission(activity)
    }

    // 切换摄像头
//    fun toggleCamera(isFrontCamera: Boolean): String? {
//        return CameraUtils.getCameraId(cameraManager, isFrontCamera)
//    }

    // 打开相机并启动预览
//    fun startCameraPreview(cameraId: String, surface: Surface) {
//        CameraUtils.openCamera(cameraId, surface)
//    }
}

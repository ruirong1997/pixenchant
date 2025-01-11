package com.project.pixenchant.camera2.manager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraDevice
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.project.pixenchant.ext.getAppContext
import javax.inject.Inject

/**
 * 摄像头操作相关的 调用系统 CAMERA_SERVICE
 */
class CameraHelper @Inject constructor() {

    private val cameraManager: CameraManager =
        getAppContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

    /**
     * 获取指定前后摄像头的 ID
     * @param isFront 是否是前置摄像头
     * @return 摄像头 ID
     * @throws RuntimeException 如果没有找到合适的摄像头
     */
    fun getCameraId(isFront: Boolean): String {
        val facing = if (isFront) CameraCharacteristics.LENS_FACING_FRONT else CameraCharacteristics.LENS_FACING_BACK
        return cameraManager.cameraIdList.firstOrNull { cameraId ->
            cameraManager.getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.LENS_FACING) == facing
        } ?: throw RuntimeException("No suitable camera found")
    }

    /**
     * 打开指定摄像头
     * @param cameraId 摄像头 ID
     * @param callback 摄像头状态回调
     * @param handler 处理器
     */
    @SuppressLint("MissingPermission")
    fun openCamera(cameraId: String, callback: CameraDevice.StateCallback, handler: Handler) {
        if (isCameraPermissionGranted()) {
            cameraManager.openCamera(cameraId, callback, handler)
        } else {
            // 这里可以考虑处理权限拒绝的情况，比如提示用户授权
        }
    }

    /**
     * 注册摄像头可用性回调
     * @param callback 可用性回调
     * @param handler 处理器
     */
    fun registerAvailabilityCallback(callback: CameraManager.AvailabilityCallback, handler: Handler?) {
        cameraManager.registerAvailabilityCallback(callback, handler)
    }

    /**
     * 注销摄像头可用性回调
     * @param callback 可用性回调
     */
    fun unregisterAvailabilityCallback(callback: CameraManager.AvailabilityCallback) {
        cameraManager.unregisterAvailabilityCallback(callback)
    }

    /**
     * 检查摄像头权限
     * @return 是否具有摄像头权限
     */
    private fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getAppContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getCameraCharacteristics(cameraId: String): CameraCharacteristics {
        return cameraManager.getCameraCharacteristics(cameraId)
    }
}

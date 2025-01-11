package com.project.pixenchant.camera2.imp

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice

interface ICameraStateListener {

    // 相机打开时的回调
    fun onCameraOpening(cameraId: String) {}

    // 相机打开时的回调
    fun onCameraOpened(camera: CameraDevice) {}

    // 相机断开连接时的回调
    fun onCameraDisconnected(camera: CameraDevice) {}

    // 相机发生错误时的回调
    fun onCameraError(camera: CameraDevice, error: Int) {}

    // 相机关闭中
    fun onCameraClosing(camera: CameraDevice) {}

    // 相机关闭
    fun onCameraClosed(camera: CameraDevice?) {}

    // 会话创建
    fun onSessionCreated(session: CameraCaptureSession) {}

    // 会话关闭
    fun onSessionClosed(session: CameraCaptureSession) {}

    // 会话失败
    fun onSessionFailed(session: CameraCaptureSession) {}


}

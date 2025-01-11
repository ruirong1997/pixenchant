package com.project.pixenchant.camera2.manager

import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Handler
import android.util.Log
import android.view.Surface
import com.project.pixenchant.ext.getAppContext

class CaptureSessionManager {

    private var cameraCaptureSession: CameraCaptureSession? = null

    /**
     * 设置当前 CameraCaptureSession
     */
    fun setCameraCaptureSession(session: CameraCaptureSession) {
        cameraCaptureSession = session
    }

    fun getCameraCaptureSession(): CameraCaptureSession? {
        return cameraCaptureSession
    }

    /**
     * 创建 CameraCaptureSession
     */
    fun createCaptureSession(
        cameraDevice: CameraDevice?,
        targetSurfaces: List<Surface>,
        callback: CameraCaptureSession.StateCallback
    ) {
        if (cameraDevice == null) {
            Log.e(TAG, "CameraDevice is null, cannot create capture session.")
            return
        }

        val sessionConfiguration = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            targetSurfaces.map { OutputConfiguration(it) },
            getAppContext().mainExecutor,
            callback
        )

        cameraDevice.createCaptureSession(sessionConfiguration)
    }

    /**
     * 设置重复请求
     */
    fun setRepeatingRequest(requestBuilder: CaptureRequest.Builder, listener: CameraCaptureSession.CaptureCallback?, handler: Handler) {
        if (cameraCaptureSession == null) {
            Log.e(TAG, "CameraCaptureSession is null, cannot set repeating request.")
            return
        }
        Log.e(TAG, "CameraCaptureSession cameraCaptureSession.")
        try {
            cameraCaptureSession?.setRepeatingRequest(requestBuilder.build(), listener, handler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to set repeating request: ${e.message}")
        }
    }

    /**
     * 停止重复请求
     */
    fun stopRepeating() {
        try {
            cameraCaptureSession?.stopRepeating()
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to stop repeating: ${e.message}")
        }
    }

    /**
     * 中止捕获
     */
    fun abortCaptures() {
        try {
            cameraCaptureSession?.abortCaptures()
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to abort captures: ${e.message}")
        }
    }

    /**
     * 捕获单次请求
     */
    fun capture(request: CaptureRequest, callback: CameraCaptureSession.CaptureCallback?, handler: Handler?) {
        if (cameraCaptureSession == null) {
            Log.e(TAG, "CameraCaptureSession is null, cannot capture.")
            return
        }

        try {
            cameraCaptureSession?.capture(request, callback, handler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to capture request: ${e.message}")
        }
    }

    /**
     * 关闭 CameraCaptureSession
     */
    fun closeSession() {
        try {
            cameraCaptureSession?.close()
            cameraCaptureSession = null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to close session: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "CameraCaptureSessionManager"
    }
}

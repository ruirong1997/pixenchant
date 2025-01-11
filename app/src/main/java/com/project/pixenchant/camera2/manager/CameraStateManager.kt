package com.project.pixenchant.camera2.manager

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.util.Log
import com.project.pixenchant.camera2.data.CameraDeviceState
import com.project.pixenchant.camera2.data.CameraState
import com.project.pixenchant.camera2.data.RequestState
import com.project.pixenchant.camera2.data.SessionState
import com.project.pixenchant.camera2.imp.ICameraStateListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 相机状态管理
 */
class CameraStateManager {

    companion object {
        private val TAG = CameraStateManager::class.java.name
    }

    private val cameraStateMap = ConcurrentHashMap<CameraDevice, CameraState>()
    private var postListener: ICameraStateListener? = null

    /**
     * 已经准备好 下一次打开
     */
    private val isPrepared = AtomicBoolean(true)

    private val listener = object : ICameraStateListener {

        override fun onCameraOpening(cameraId: String) {
            Log.d(TAG, "onCameraOpening :$cameraId")
            isPrepared.set(false)
            postListener?.onCameraOpening(cameraId)
        }

        override fun onCameraOpened(camera: CameraDevice) {
            Log.d(TAG, "onCameraOpened :$camera")
            isPrepared.set(false)
            postListener?.onCameraOpened(camera)
            updateDevState(camera, devState = CameraDeviceState.ON_OPENED)
        }

        override fun onCameraDisconnected(camera: CameraDevice) {
            Log.d(TAG, "onCameraDisconnected :$camera")
            isPrepared.set(true)
            postListener?.onCameraDisconnected(camera)
            updateDevState(camera, devState = CameraDeviceState.ON_DISCONNECTED)
        }

        override fun onCameraError(camera: CameraDevice, error: Int) {
            Log.d(TAG, "onCameraError :$camera")
            isPrepared.set(true)
            postListener?.onCameraError(camera, error)
            updateDevState(camera, devState = CameraDeviceState.ON_ERROR)
        }

        override fun onCameraClosing(camera: CameraDevice) {
            Log.d(TAG, "onCameraClosing :$camera")
            isPrepared.set(true)
            postListener?.onCameraClosing(camera)
            updateDevState(camera, devState = CameraDeviceState.ON_CLOSING)
        }

        override fun onCameraClosed(camera: CameraDevice?) {
            Log.d(TAG, "onCameraClosed :$camera")
            isPrepared.set(true)
            postListener?.onCameraClosed(camera)
            camera?.let { updateDevState(it, devState = CameraDeviceState.ON_CLOSED) }
            cameraStateMap.remove(camera)
        }

        override fun onSessionCreated(session: CameraCaptureSession) {
            isPrepared.set(true)
            updateDevState(session.device, sessionState = SessionState.CONFIGURING)
            Log.d(TAG, "onSessionCreated :$session")
        }

        override fun onSessionClosed(session: CameraCaptureSession) {
            isPrepared.set(true)
            updateDevState(session.device, sessionState = SessionState.CLOSED)
            postListener?.onSessionClosed(session)
            Log.d(TAG, "onSessionClosed :$session")
        }


        override fun onSessionFailed(session: CameraCaptureSession) {
            isPrepared.set(true)
            updateDevState(session.device, sessionState = SessionState.CLOSED)
            postListener?.onSessionFailed(session)
            Log.d(TAG, "onSessionFailed :$session")
        }
    }

    fun getListener(): ICameraStateListener {
        return listener
    }

    fun isPrepared(): Boolean{
        Log.d(TAG, "isOpeningCamera :$isPrepared")
        return !isPrepared.get()
    }

    private fun clearCameraMaps() {
        cameraStateMap.clear()
    }

    private fun updateDevState(cameraDevice: CameraDevice, devState: CameraDeviceState? = null, sessionState: SessionState? = null, requestState: RequestState? = null) {
        cameraDevice.let { device ->
            val currentState = cameraStateMap.getOrPut(device) { CameraState() }
            devState?.let { currentState.deviceState = it }
            sessionState?.let { currentState.sessionState = it }
            requestState?.let { currentState.requestState = it }
            cameraStateMap[device] = currentState
        }
    }

    private fun getCurDevState(cameraDevice: CameraDevice): CameraState? {
        return cameraStateMap[cameraDevice]
    }

    fun setSessionListener(listener: ICameraStateListener) {
        postListener = listener
    }
}
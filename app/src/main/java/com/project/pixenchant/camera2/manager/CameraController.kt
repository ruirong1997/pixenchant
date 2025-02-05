package com.project.pixenchant.camera2.manager

import android.annotation.SuppressLint
import android.hardware.camera2.*
import android.media.Image
import android.util.Log
import android.view.Surface
import com.project.pixenchant.camera2.ext.setOnClosedListener
import com.project.pixenchant.camera2.imp.ICameraStateListener
import com.project.pixenchant.camera2.manager.CameraManager.Companion
import com.project.pixenchant.thread.BackgroundThreadManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Camera2 控制器：负责相机的打开、预览、拍照及会话管理。
 */
@Singleton
class CameraController @Inject constructor(
    private val stateManager: CameraStateManager,
    val backgroundThreadManager: BackgroundThreadManager = BackgroundThreadManager("")
) {

    companion object {
        private val TAG = CameraController::class.java.name
    }

    // region - 相机设备变量
    private var cameraDevice: CameraDevice? = null // 当前相机设备实例

    private val cameraHelper = CameraHelper() // 相机帮助类，用于获取相机信息
    private val cameraCaptureSession = CaptureSessionManager() // 相机会话管理

    private var cameraStateListener: ICameraStateListener? = null // 相机状态监听器
    // endregion

    init {
        setCameraStateListener(stateManager.getListener())
    }

    // region - 设置与获取方法

    // region - 设置与获取方法
    private fun setCameraStateListener(listener: ICameraStateListener) {
        this.cameraStateListener = listener // 设置状态监听器
    }

    fun getCameraId(isFront: Boolean): String = cameraHelper.getCameraId(isFront) // 获取前/后置相机 ID

    fun getCameraDev(): CameraDevice? = cameraDevice // 获取当前相机设备实例
    // endregion

    // region - 相机打开与关闭
    /**
     * 打开相机，支持挂起等待结果。
     * @param cameraId 相机 ID
     */
    @SuppressLint("MissingPermission")
    suspend fun openCamera(cameraId: String): Boolean {
        val cameraOpenResult = CompletableDeferred<Boolean>()
        cameraStateListener?.onCameraOpening(cameraId)
        backgroundThreadManager.startThread {
            cameraHelper.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    Log.d("Camera2Manager", "onOpened :$camera")
                    cameraDevice = camera
                    cameraStateListener?.onCameraOpened(camera)
                    cameraOpenResult.complete(true)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.d("Camera2Manager", "onDisconnected :$camera")
                    cameraOpenResult.complete(false)
                    cameraStateListener?.onCameraDisconnected(camera)
                    closeCameraDevice()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.d("Camera2Manager", "onError :$error")
                    cameraOpenResult.complete(false)
                    cameraStateListener?.onCameraDisconnected(camera)
                    closeCameraDevice()
                }

                override fun onClosed(camera: CameraDevice) {
                    Log.d("Camera2Manager", "OnClosed :$camera")
                    cameraStateListener?.onCameraClosed(camera)
                }
            }, backgroundThreadManager.getHandler())
        }

        return cameraOpenResult.await()
    }

    /**
     * 关闭相机资源并等待释放完成。
     */
    suspend fun closeCameraAndWait(): Boolean {
        closeSessionAndWait()
        closeCameraDeviceAndWait()
        return true
    }

    /**
     * 关闭相机设备并等待。
     */
    private suspend fun closeCameraDeviceAndWait(): Boolean {
        cameraDevice ?: return false
        val cameraCloseResult = CompletableDeferred<Boolean>()
        stateManager.setSessionListener(object : ICameraStateListener {
            override fun onCameraClosed(camera: CameraDevice?) {
                Log.d(TAG,"closeCameraDeviceAndWait onCameraClosed :$camera")
                cameraCloseResult.complete(true)
            }
        })
        closeCameraDevice()
        return cameraCloseResult.await()
    }

    /**
     * 关闭相机设备。
     */
    fun closeCameraDevice() {
        Log.d(TAG, "closeCameraDevice")
        try {
            cameraDevice?.close() // 关闭相机设备
            cameraDevice = null // 将引用置为 null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing camera: ${e.message}")
        }
    }

    /**
     * 关闭相机会话并等待。
     */
    suspend fun closeSessionAndWait(): Boolean {
        cameraDevice ?: return false
        val cameraCloseResult = CompletableDeferred<Boolean>()
        stateManager.setSessionListener(object : ICameraStateListener {

            override fun onSessionClosed(session: CameraCaptureSession) {
                cameraCloseResult.complete(true)
            }

            override fun onSessionFailed(session: CameraCaptureSession) {
                Log.d("CameraStateManager", "2222 onSessionClosed :$session")
                cameraCloseResult.complete(true)
            }
        })
        closeSession()
        return cameraCloseResult.await()
    }

    /**
     * 关闭会话。
     */
    fun closeSession() {
        Log.d(TAG, "CameraCaptureSessionManager closeSession")
        try {
            cameraCaptureSession.stopRepeating()
            cameraCaptureSession.abortCaptures()
        } catch (e: CameraAccessException) {
            Log.e("CameraCaptureSessionManager", "Failed to stop preview: ${e.message}", e)
        } finally {
            cameraCaptureSession.closeSession()
        }
    }

    // endregion

    /**
     * 开启预览，绑定 Surface。
     */
    fun startPreview(surfaceList: List<Surface>) {
        cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)?.apply {
            surfaceList.forEachIndexed { _, surface ->
                addTarget(surface)
            }
            createCaptureSession(surfaceList, this)
        }
    }

    /**
     * 开启预览并等待
     */
    suspend fun startPreviewAndWait(surfaceList: List<Surface>) {
        cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)?.apply {
            surfaceList.forEachIndexed { _, surface ->
                addTarget(surface)
            }
            createCaptureSessionAndWait(surfaceList, this)
        }
    }

    /**
     * 停止预览，释放会话资源。
     */
    fun stopPreview() {
        try {
            cameraCaptureSession.stopRepeating()
            cameraCaptureSession.abortCaptures()
        } catch (e: CameraAccessException) {
            Log.e("CameraCaptureSessionManager", "Failed to stop preview: ${e.message}", e)
        } finally {
            cameraCaptureSession.closeSession()
        }
    }
    // endregion

    // region - 会话管理
    /**
     * 创建相机会话。
     */
    private fun createCaptureSession(targetSurfaces: List<Surface>, builder: CaptureRequest.Builder) {
        cameraDevice?.let { device ->
            cameraCaptureSession.createCaptureSession(
                device, targetSurfaces, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        Log.e(TAG, "ConConfigured.  Device: ${session.device}")
                        cameraCaptureSession.setCameraCaptureSession(session)
                        cameraCaptureSession.setRepeatingRequest(builder, null, backgroundThreadManager.getHandler())
                        cameraStateListener?.onSessionCreated(session)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        cameraStateListener?.onSessionFailed(session)
                        Log.e(TAG, "Capture session configuration failed.  Device: ${session.device}")
                    }

                    override fun onClosed(session: CameraCaptureSession) {
                        cameraStateListener?.onSessionClosed(session)
                        Log.d(TAG, "Capture session closed.  Device: ${session.device}")
                    }
                }
            )
        }
    }

    // region - 会话管理
    /**
     * 创建相机会话并等待。
     */
    private suspend fun createCaptureSessionAndWait(
        targetSurfaces: List<Surface>,
        builder: CaptureRequest.Builder
    ): Boolean {
        val createSessionResult = CompletableDeferred<Boolean>()
        cameraDevice?.let { device ->
            cameraCaptureSession.createCaptureSession(
                device, targetSurfaces, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        Log.e(TAG, "ConConfigured.  Device: ${session.device}")
                        cameraCaptureSession.setCameraCaptureSession(session)
                        cameraCaptureSession.setRepeatingRequest(builder, null, backgroundThreadManager.getHandler())
                        cameraStateListener?.onSessionCreated(session)
                        createSessionResult.complete(true)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        cameraStateListener?.onSessionFailed(session)
                        createSessionResult.complete(false)
                        Log.e(TAG, "Capture session configuration failed.  Device: ${session.device}")
                    }

                    override fun onClosed(session: CameraCaptureSession) {
                        cameraStateListener?.onSessionClosed(session)
                        createSessionResult.complete(false)
                        Log.d(TAG, "Capture session closed.  Device: ${session.device}")
                    }
                }
            )
        } ?: return false
        return createSessionResult.await()
    }
    // endregion

    // region - 拍照相关
    /**
     * 拍照并返回 Bitmap。
     */
    fun capturePhoto(surface: Surface) {
        cameraDevice?.apply {
            val captureRequestBuilder = this.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                addTarget(surface)
            }
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
        }
    }
    // endregion

    // region - GPU 渲染
    /**
     * 使用 GPU 处理图像。
     */
    private fun processWithGPU(image: Image) {
        val yPlane = image.planes[0].buffer
        val uvPlane = image.planes[1].buffer
//        gpuRenderer.render(yPlane, uvPlane)
    }
    // endregion

    // region - 资源释放
    /**
     * 释放资源。
     */
    fun release() {
        closeSession()
        closeCameraDevice()
    }

    /**
     * 获取指定相机 ID 的相机特性。
     * @param cameraId 相机 ID
     * @return 相机特性对象
     */
    fun getCameraCharacteristics(cameraId: String): CameraCharacteristics {
        return cameraHelper.getCameraCharacteristics(cameraId)
    }
    // endregion
}

package com.project.pixenchant.camera2.manager

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import com.project.pixenchant.camera2.renderer.CameraRenderer
import com.project.pixenchant.ext.getAppContext
import com.project.pixenchant.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton


/**
 * 统筹管理
 */
@Singleton
class CameraManager @Inject constructor() {

    // CameraController 用于管理相机操作
    private val cameraStateManager = CameraStateManager()
    private val cameraController = CameraController(cameraStateManager)

    private val windowManager = getAppContext().getSystemService(WindowManager::class.java)
    private val cameraImageRender = CameraImageRender()
//    private val renderer = CameraRenderer()
    private val renderer = CameraRenderer()

    // 用于管理协程任务的作用域
    private val cameraScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // 当前是否使用前置摄像头
    private var isUsingFrontCamera = false

    // 用于标识相机切换状态
    private val isCameraSwitching = AtomicBoolean(false)

    /**
     * 获取当前摄像头ID（根据是否使用前置摄像头）
     */
    fun getCameraId(useFrontCamera: Boolean = isUsingFrontCamera): String =
        cameraController.getCameraId(useFrontCamera)

    /**
     * 获取当前打开的相机设备
     */
    fun getCameraDevice(): CameraDevice? = cameraController.getCameraDev()

    /**
     * 打开相机并绑定到指定的 TextureView
     */
    suspend fun openCameraAndWait(surfaceTexture: SurfaceTexture, cameraId: String = getCameraId()) {
        // 异步打开相机
        if (cameraStateManager.isPrepared()) return
        if (cameraController.openCamera(cameraId)) {
            startPreviewAndWait(surfaceTexture)
        }
    }

    /**
     * 打开相机并绑定到指定的 TextureView
     */
    fun openCamera(surfaceTexture: SurfaceTexture, cameraId: String = getCameraId()) {

        cameraScope.launch {
            // 异步打开相机
            if (cameraStateManager.isPrepared()) return@launch
            if (cameraController.openCamera(cameraId)) {
                startPreview(surfaceTexture)

            }
        }
    }


    /**
     * 切换摄像头（前后置切换）
     */
    fun switchCamera(surfaceTexture: SurfaceTexture, onSwitchCompleted: (() -> Unit)? = null) {
        Log.d(TAG, "Attempting to switch camera. Current state: ${isCameraSwitching.get()}")
        if (!isCameraSwitching.compareAndSet(false, true)) {
            Log.d(TAG, "Camera is already switching, ignoring request.")
            return
        }

        cameraScope.launch {
            try {
                renderer.pauseRendering()
                closeCameraAndWait()
                isUsingFrontCamera = !isUsingFrontCamera
                openCameraAndWait(surfaceTexture, getCameraId(isUsingFrontCamera))
                delay(SWITCH_DELAY_TIME)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to switch camera: ${e.message}", e)
            } finally {
                isCameraSwitching.set(false)
                Log.d(TAG, "Camera switching process completed.")
                renderer.resumeRendering()
                onSwitchCompleted?.invoke() // 通知切换完成
            }
        }
    }


    /**
     * 关闭当前相机
     */
    suspend fun closeCameraAndWait() {
        // 关闭相机
        cameraController.closeCameraAndWait()
    }

    fun closeCameraDevice() {
        // 关闭相机
        cameraController.closeCameraDevice()
    }

    /**
     * 释放资源，取消所有协程任务
     */
    fun release() {
        cameraScope.cancel()
    }
    /**
     * 开始相机预览
     */
    fun startPreview(surfaceTexture: SurfaceTexture) {
        renderer.setRotationAngle(getRotationAngle().toFloat(), isUsingFrontCamera)
        val previewSize = getBestPreviewSize(getCameraId(isUsingFrontCamera), 1080, 2160)
        surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)
        cameraImageRender.initPreviewRenderer(1080,2160)

//        textureView.surfaceTexture?.let { texture ->
//            texture.setDefaultBufferSize(textureView.width, textureView.height)
//            cameraImageRender.initCaptureReader(textureView)
//            cameraImageRender.initPreviewReader(textureView)
//            val captureSurface = listOf(
//                cameraImageRender.getPreviewReader().surface,
//                cameraImageRender.getCaptureRender().surface
//            )
//            cameraController.startPreview(captureSurface)
//        }
        val previewSurface = Surface(surfaceTexture)
        val surfaceList =
            listOf(previewSurface/*, cameraImageRender.getCaptureRender().surface*/)


        cameraController.startPreview(surfaceList)
    }

    suspend fun startPreviewAndWait(surfaceTexture: SurfaceTexture) {
        renderer.setRotationAngle(getRotationAngle().toFloat(), isUsingFrontCamera)
        val previewSize = getBestPreviewSize(getCameraId(isUsingFrontCamera), 1080, 2160)
        surfaceTexture.setDefaultBufferSize(previewSize.width,previewSize.height)
        val previewSurface = Surface(surfaceTexture)
        val surfaceList =
            listOf(previewSurface/*, cameraImageRender.getCaptureRender().surface*/)

//            texture.setDefaultBufferSize(textureView.width, textureView.height)
//            cameraImageRender.initCaptureReader(textureView)
//            cameraImageRender.initPreviewReader(textureView)
        cameraController.startPreviewAndWait(surfaceList)

    }

    /**
     * 停止相机预览
     */
    fun stopPreview() {
        cameraController.stopPreview()
    }


    fun getBestPreviewSize(cameraId: String, targetWidth: Int, targetHeight: Int): Size {
        try {
            // 获取摄像头的特性
            val characteristics = getCameraCharacteristics(cameraId)

            // 获取支持的预览尺寸
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val sizes = map?.getOutputSizes(SurfaceTexture::class.java)

            // 如果没有找到合适的尺寸，直接返回默认尺寸
            sizes?.let {
                // 找到与目标宽高最接近的尺寸
                var bestSize: Size? = null
                var minDifference = Int.MAX_VALUE

                for (size in sizes) {
                    // 计算尺寸差异（基于面积或比例）
                    val widthDifference = Math.abs(size.width - targetWidth)
                    val heightDifference = Math.abs(size.height - targetHeight)
                    val totalDifference = widthDifference + heightDifference

                    // 选择差异最小的尺寸
                    if (totalDifference < minDifference) {
                        minDifference = totalDifference
                        bestSize = size
                    }
                }
                return bestSize ?: sizes[0] // 如果没有找到最优尺寸，返回第一个尺寸
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return Size(targetWidth, targetHeight) // 如果无法获取到最佳尺寸，返回默认尺寸
    }

    /**
     * 拍照并返回结果
     * @param onPhotoCaptured 拍照完成后回调，传入 Bitmap 对象
     */
    fun capturePhoto(onPhotoCaptured: (Bitmap) -> Unit) {
        cameraImageRender.getCaptureRender().apply {
            cameraController.capturePhoto(surface)
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireNextImage()
                BitmapUtils.yuvToBitmap(image)?.let { onPhotoCaptured(it) }
                image.close()
            }, null)
        }
    }

    /**
     * 获取相机特性
     * @param cameraId 摄像头ID
     */
    fun getCameraCharacteristics(cameraId: String): CameraCharacteristics {
        return cameraController.getCameraCharacteristics(cameraId)
    }

    fun getRenderer(): CameraRenderer{
        return renderer
    }

    fun getRotationAngle(): Int {
        val rotation = windowManager.defaultDisplay.rotation
        val cameraCharacteristics = getCameraCharacteristics(getCameraId(isUsingFrontCamera))
        // 获取相机的传感器方向
        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        Log.d("CameraPreview", "cameraCharacteristics windowManagerRotation:${rotation}   ----  sensorOrientation :$sensorOrientation")

        // 转换屏幕方向到角度
        val deviceOrientation = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        // 计算最终角度
        return if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            // 前置摄像头需要额外翻转
            (sensorOrientation + deviceOrientation) % 360
        } else {
            // 后置摄像头直接相加
            (sensorOrientation - deviceOrientation + 360) % 360
        }
    }

    private fun getCameraResolution(): Size {
        val characteristics = getCameraCharacteristics(getCameraId(isUsingFrontCamera))

        // 获取支持的最大分辨率
        val configurations = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val outputSizes = configurations?.getOutputSizes(SurfaceTexture::class.java)

        // 选择最大分辨率
        val maxResolution = outputSizes?.maxByOrNull { it.width * it.height }
        Log.d("CameraPreview", "maxResolution : $maxResolution")
        return maxResolution ?: Size(1920, 1080) // 如果没有获取到，使用默认的 1920x1080
    }

    companion object {
        private const val TAG = "Camera2Manager"

        private const val SWITCH_DELAY_TIME = 300L
    }
}

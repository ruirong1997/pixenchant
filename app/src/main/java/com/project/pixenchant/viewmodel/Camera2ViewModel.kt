package com.project.pixenchant.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.pixenchant.camera2.data.CameraMode
import com.project.pixenchant.camera2.data.CameraModeItem
import com.project.pixenchant.camera2.data.MediaType
import com.project.pixenchant.camera2.manager.CameraManager
import com.project.pixenchant.camera2.model.CameraRepository
import com.project.pixenchant.camera2.model.MediaRepository
import com.project.pixenchant.camera2.renderer.CameraRenderer
import com.project.pixenchant.ext.getAppContext
import com.project.pixenchant.utils.BitmapUtils
import com.project.pixenchant.utils.CameraUtils.getCaptureOrientation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Camera2ViewModel @Inject constructor(
    val cameraRepository: CameraRepository,
    val mediaRepository: MediaRepository
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private var surfaceTexture: SurfaceTexture? = null

    @Inject
    lateinit var cameraManager: CameraManager

    private val windowManager = getAppContext().getSystemService(WindowManager::class.java)

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission = _hasCameraPermission

    val cameraMode = cameraRepository.curCameraMode

    val showMediaType = mediaRepository.showMediaType
    val mediaList = mediaRepository.mediaList
    val videoList = mediaRepository.videoList
    val imageList = mediaRepository.imageList

    fun setSurfaceView(view: SurfaceTexture) {
        surfaceTexture = view
    }

    fun updatePermissionStatus(granted: Boolean) {
        _hasCameraPermission.value = granted
    }

    fun getCurCameraId(): String {
        return cameraManager.getCameraId()
    }

    /**
     * 打开摄像头
     */
    fun openCamera(cameraId: String = getCurCameraId()) {
        surfaceTexture?.let {
            cameraManager.openCamera(it, cameraId)
        }
    }

    fun closeCamera() {
        cameraManager.closeCameraDevice()
    }


    /**
     * 切换摄像头
     */
    fun switchCamera() {
        surfaceTexture?.let {
            cameraManager.switchCamera(it)
        }
    }

    fun getCameraModeList(): List<CameraModeItem> {
        return cameraRepository.cameraModeList
    }

    fun setCameraMode(mode: CameraMode) {
        cameraRepository.setCameraMode(mode)
    }

    /**************************图库**************************/
    fun openMediaStorage(type: MediaType = MediaType.ALL) {
        viewModelScope.launch {
            mediaRepository.updateMediaType(type)
        }
    }

    fun syncMediaStorage() {
        viewModelScope.launch {
            mediaRepository.updateMediaType(MediaType.SYNC)
        }
    }

    fun closeMediaStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaRepository.updateMediaType(MediaType.CLOSE)
            syncMediaStorage()
        }
    }

    fun startPreview() {
        Log.d("Camera2ViewModel", "StartPreView")
        surfaceTexture?.let { cameraManager.startPreview(it) }
    }

    fun stopPreview() {
        cameraManager.stopPreview()
    }

    // 捕捉照片并自动旋转到正确角度
    fun capturePhoto(onPhotoCaptured: (Bitmap) -> Unit) {
        val rotation = windowManager.defaultDisplay.rotation
        val orientation = getCaptureOrientation(rotation, cameraManager.getCameraCharacteristics(getCurCameraId()))

        cameraManager.capturePhoto { bitmap ->
            val rotatedBitmap = BitmapUtils.rotateBitmap(bitmap, orientation)
            onPhotoCaptured(rotatedBitmap)
        }
    }

    fun getRenderer(): CameraRenderer {
        return cameraManager.getRenderer()
    }

    override fun onCleared() {
        surfaceTexture = null
        super.onCleared()
    }

    fun openFilter() {
    }
}




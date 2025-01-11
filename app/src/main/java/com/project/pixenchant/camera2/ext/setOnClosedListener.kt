package com.project.pixenchant.camera2.ext

import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import com.project.pixenchant.camera2.manager.CameraHelper

fun CameraDevice.setOnClosedListener(
    cameraManager: CameraHelper,
    onClosed: () -> Unit
    ) {
        val availabilityCallback = object : CameraManager.AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                if (cameraId == this@setOnClosedListener.id) {
                    onClosed()
                    cameraManager.unregisterAvailabilityCallback(this)
                }
            }
        }
        cameraManager.registerAvailabilityCallback(availabilityCallback, null)
    }
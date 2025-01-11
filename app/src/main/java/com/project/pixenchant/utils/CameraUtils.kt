package com.project.pixenchant.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.camera2.CameraCharacteristics
import android.media.Image
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream

object CameraUtils {


    fun getCaptureOrientation(rotation: Int,cameraCharacteristics: CameraCharacteristics): Int {

        // 获取相机的传感器方向
        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

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

}
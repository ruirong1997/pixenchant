package com.project.pixenchant.camera2.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.SurfaceTexture
import android.graphics.YuvImage
import android.media.Image
import android.media.ImageReader
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.project.pixenchant.thread.BackgroundThreadManager
import com.project.pixenchant.utils.BitmapUtils.renderBitmapToTextureView
import com.project.pixenchant.utils.BitmapUtils.yuvToBitmap
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class CameraImageRender {

    companion object {
        private val TAG = CameraImageRender::class.java.name.toString()
    }

    val backgroundThreadManager: BackgroundThreadManager = BackgroundThreadManager("CameraImageRender")


    private var captureImageReader: ImageReader? = null

    private var previewReader: ImageReader? = null


    fun getCaptureRender(): ImageReader {
        return captureImageReader ?: throw RuntimeException("Need Init ImageReaderManager")
    }

}
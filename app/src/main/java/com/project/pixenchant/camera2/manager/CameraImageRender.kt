package com.project.pixenchant.camera2.manager

import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.util.Log
import com.project.pixenchant.thread.BackgroundThreadManager
import java.util.concurrent.ExecutorService


class CameraImageRender {

    companion object {
        private val TAG = CameraImageRender::class.java.name.toString()
    }

    /** Blocking ML operations are performed using this executor */
    val backgroundThreadManager: BackgroundThreadManager = BackgroundThreadManager("CameraImageRender")

    private var captureImageReader: ImageReader? = null

    private var previewReader: ImageReader? = null

    fun initPreviewRenderer(width: Int, height: Int, processImage: ((Image) -> Unit)? = null) {
        backgroundThreadManager.startThread {
            Log.d("CameraImageRender", "width :${width} -- height:$height")
            previewReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2) // 增大缓存
            previewReader?.setOnImageAvailableListener({ reader ->
                var image: Image? = null
                try {
                    image = reader.acquireLatestImage()
                    if (image != null) {
                        processImage?.invoke(image)  // 传递 Image 处理
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    image?.close() // 确保 image 被关闭，防止内存泄漏
                }
            }, backgroundThreadManager.getHandler())
        }
    }


    fun getPreviewRender(): ImageReader {
        return previewReader ?: throw RuntimeException("Need Init ImageReaderManager")
    }

    fun getCaptureRender(): ImageReader {
        return captureImageReader ?: throw RuntimeException("Need Init ImageReaderManager")
    }

}
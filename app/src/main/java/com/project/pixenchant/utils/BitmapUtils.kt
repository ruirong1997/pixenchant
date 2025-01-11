package com.project.pixenchant.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.view.TextureView
import java.io.ByteArrayOutputStream
import kotlin.math.max

object BitmapUtils {

    fun yuvToBitmap(image: Image): Bitmap? {
        val yuvPlanes = image.planes
        val yPlane = yuvPlanes[0]
        val uPlane = yuvPlanes[1]
        val vPlane = yuvPlanes[2]

        val ySize = yPlane.buffer.remaining()
        val uSize = uPlane.buffer.remaining()
        val vSize = vPlane.buffer.remaining()

        val yuvData = ByteArray(ySize + uSize + vSize)
        yPlane.buffer.get(yuvData, 0, ySize)
        uPlane.buffer.get(yuvData, ySize, uSize)
        vPlane.buffer.get(yuvData, ySize + uSize, vSize)

        val yuvImage = YuvImage(
            yuvData, ImageFormat.NV21, image.width, image.height, null
        )

        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, outputStream)

        val imageBytes = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat()) // 将旋转角度转换为 float 类型
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    fun applyCustomBlur(bitmap: Bitmap, radius: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        // 获取图像的像素数据
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 创建一个输出像素数组
        val outputPixels = IntArray(width * height)

        // 计算模糊效果
        for (y in 0 until height) {
            for (x in 0 until width) {
                var r = 0
                var g = 0
                var b = 0
                var count = 0

                // 计算周围像素的平均值
                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val nx = x + dx
                        val ny = y + dy

                        // 确保不会越界
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            val pixel = pixels[ny * width + nx]
                            r += Color.red(pixel)
                            g += Color.green(pixel)
                            b += Color.blue(pixel)
                            count++
                        }
                    }
                }

                // 计算平均颜色并设置到输出像素
                val avgR = r / count
                val avgG = g / count
                val avgB = b / count

                // 使用 RGB 值生成新的像素
                outputPixels[y * width + x] = Color.rgb(avgR, avgG, avgB)
            }
        }

        // 将模糊后的像素设置回新的 Bitmap
        outputBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)

        return outputBitmap
    }


    fun renderBitmapToTextureView(bitmap: Bitmap, textureView: TextureView) {
        val surfaceTexture = textureView.surfaceTexture ?: return
        val canvas = textureView.lockCanvas() ?: return

        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val textureViewWidth = textureView.width
        val textureViewHeight = textureView.height

        // 计算缩放比例，保持宽高比一致
        val scaleX = textureViewWidth.toFloat() / bitmapWidth
        val scaleY = textureViewHeight.toFloat() / bitmapHeight
        val scale = max(scaleX, scaleY)  // 取较大的缩放比例，保证全屏显示

        val offsetX = (textureViewWidth - bitmapWidth * scale) / 2
        val offsetY = (textureViewHeight - bitmapHeight * scale) / 2

        // 使用 Matrix 进行缩放和平移
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate(offsetX, offsetY)
        }

        // 绘制 Bitmap 到 Canvas
        canvas.drawBitmap(bitmap, matrix, null)

        // 解锁并提交 Canvas
        textureView.unlockCanvasAndPost(canvas)
    }


    fun yuvToBitmap(image: Image, rotationDegree: Int): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uvSize = uBuffer.remaining()

        // 创建一个 NV21 字节数组
        val nv21 = ByteArray(ySize + uvSize * 2)

        // 填充 Y、UV 数据
        yBuffer.get(nv21, 0, ySize)
        for (i in 0 until uvSize) {
            nv21[ySize + i * 2] = vBuffer.get(i)
            nv21[ySize + i * 2 + 1] = uBuffer.get(i)
        }

        // 获取原始图像的宽高
        val originalWidth = image.width
        val originalHeight = image.height

        // 将 NV21 转为 JPEG
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, originalWidth, originalHeight, null)
        val outStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, originalWidth, originalHeight), 100, outStream)

        val byteArray = outStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        // 根据旋转角度对 Bitmap 进行旋转
        return rotateBitmap(bitmap, rotationDegree)
    }
}
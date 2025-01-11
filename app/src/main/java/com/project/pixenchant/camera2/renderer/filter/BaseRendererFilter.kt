package com.project.pixenchant.camera2.renderer.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.project.pixenchant.R

import com.project.pixenchant.camera2.renderer.imp.IBaseFilter
import com.project.pixenchant.camera2.renderer.manager.BufferManager
import com.project.pixenchant.ext.createFloatBuffer
import com.project.pixenchant.ext.getAppContext
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class BaseRendererFilter: IBaseFilter {

    val mBufferManager = BufferManager.instance
    var mProgram = 0


    private var mRotationMatrix = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
    private var mRotationAngle: Float = 0f

    private var mVertices: FloatBuffer = createFloatBuffer(
        floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
        )
    )

   private var mTexCoords: FloatBuffer = createFloatBuffer(
        floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        )
    )

    var mWidth = 0
    var mHeight = 0

    fun setVertices(buffer: FloatBuffer) {
        mVertices = buffer
    }

    fun setTexCoords(buffer: FloatBuffer) {
        mTexCoords = buffer
    }

    fun getRotationMatrix(): FloatArray {
        return mRotationMatrix
    }

    override fun setRotationAngle(angle: Float, frontCamera: Boolean) {
        if (mRotationAngle == angle) return
        mRotationAngle = angle

        Matrix.setIdentityM(mRotationMatrix, 0)
        Matrix.rotateM(mRotationMatrix, 0, -mRotationAngle, 0f, 0f, 1f)
        if (frontCamera) {
            Matrix.scaleM(mRotationMatrix, 0, 1f, -1f, 1f)
        }
    }

    fun createTexture(): Int {
        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        GLES30.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0])

        GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST.toFloat())
        GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())

        GLES30.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        return textures[0]
    }

    fun createBitmapTexture(bitmap: Bitmap, scaleWidth: Int, scaleHeight: Int): Int {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, false)

        val textureHandle = IntArray(1)
        GLES30.glGenTextures(1, textureHandle, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, scaledBitmap, 0)

        return textureHandle[0]
    }

    fun enableVertexAttrib() {
        enableVertexAttrib(0, mVertices)
        enableVertexAttrib(1, mTexCoords)
    }

    fun bindAndDrawTexture(textureUnit: Int, textureId: Int, textureTarget: Int = GL_TEXTURE_EXTERNAL_OES) {
        GLES30.glActiveTexture(textureUnit) // 激活指定的纹理单元
        GLES30.glBindTexture(textureTarget, textureId) // 绑定指定目标的纹理
        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
    }

    fun disableAttrib(){
        // 禁用位置为 0 的顶点属性（顶点位置）
        GLES30.glDisableVertexAttribArray(0)
        // 禁用位置为 1 的顶点属性（顶点颜色或其他）
        GLES30.glDisableVertexAttribArray(1)
    }

    private fun enableVertexAttrib(index: Int, buffer: FloatBuffer) {
        GLES30.glEnableVertexAttribArray(index)
        GLES30.glVertexAttribPointer(index, 2, GLES30.GL_FLOAT, false, 0, buffer)
    }

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onTextureIdAvailable: ((Int) -> Unit)?
    ) {
        GLES30.glClearColor(0f, 0f, 0f, 1f) // 设置背景色为黑色

        val textureId = createTexture()
        mBufferManager.setTextureId(textureId)
        onTextureIdAvailable?.invoke(textureId)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        mWidth = width
        mHeight = height
        mProgram = getProgram()
    }

    override fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
    }

    abstract fun getProgram(): Int

    override fun setUpUniforms(rotationMatrix: FloatArray) {

    }


}
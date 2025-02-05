package com.project.pixenchant.camera2.renderer

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.Image
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.manager.RenderManager
import javax.inject.Inject
import javax.inject.Singleton
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 处理预览流
 */
@Singleton
class CameraRenderer @Inject constructor() : GLSurfaceView.Renderer {

    companion object {
        private val TAG = this::class.java.name
    }

    @Inject lateinit var mRenderManager: RenderManager

    var mOnSurfaceTextureAvailable: ((SurfaceTexture) -> Unit)? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mIsRenderingPaused = false

    private var mOriginBitmap: Bitmap? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val listener: ((Int) -> Unit) = { textureId ->
            Log.d(TAG, "onSurfaceCreated")
            mSurfaceTexture = SurfaceTexture(textureId)
            mSurfaceTexture?.apply {
                mOnSurfaceTextureAvailable?.invoke(this)
            }
        }
        mRenderManager.onSurfaceCreated(gl, config, listener)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mRenderManager.onSurfaceChanged(gl, width, height)
        Log.d(TAG, "onSurfaceChanged: width=$width, height=$height")
    }

    override fun onDrawFrame(gl: GL10?) {
        if (mIsRenderingPaused) return

        if (isVideoStream()) {
            mSurfaceTexture?.updateTexImage()
        }

        mRenderManager.onDrawFrame(gl, mSurfaceTexture, mOriginBitmap)

        // 检查 OpenGL 错误
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error: $error")
        }
    }

    private fun isVideoStream(): Boolean {
        return mRenderManager.isVideoStream()
    }

    fun setRotationAngle(angle: Float, isFrontCamera: Boolean = false) {
        mRenderManager.setRotationAngle(angle, isFrontCamera)
    }

    fun pauseRendering() {
        mIsRenderingPaused = true
        mRenderManager.pauseRendering()
    }

    fun resumeRendering() {
        mIsRenderingPaused = false
        mRenderManager.resumeRendering()
    }

    fun setFilterType(type: FilterType) {
        mRenderManager.setFilterType(type)
    }

    fun updateImage(bitmap: Bitmap) {
        mOriginBitmap = bitmap
    }

}

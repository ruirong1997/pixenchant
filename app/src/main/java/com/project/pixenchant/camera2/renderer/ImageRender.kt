package com.project.pixenchant.camera2.renderer

import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.manager.ProgramManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 处理图片
 */
class ImageRender : GLSurfaceView.Renderer {

    companion object {
        private val TAG = this::class.java.name
    }

    private var mProgramManager = ProgramManager()

    var mOnSurfaceTextureAvailable: ((SurfaceTexture) -> Unit)? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mIsRenderingPaused = false

    init {
        setProgramType(FilterType.BLUR)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val listener: ((Int) -> Unit) = { textureId ->
            Log.d(TAG, "onSurfaceCreated")
            mSurfaceTexture = SurfaceTexture(textureId)
            mSurfaceTexture?.apply {
                mOnSurfaceTextureAvailable?.invoke(this)
            }
        }
        mProgramManager.onSurfaceCreated(gl, config, listener)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mProgramManager.onSurfaceChanged(gl, width, height)
        Log.d(TAG, "onSurfaceChanged: width=$width, height=$height")
    }

    override fun onDrawFrame(gl: GL10?) {
        if (mIsRenderingPaused) return
        mProgramManager.onDrawFrame(gl, mSurfaceTexture)

        // 检查 OpenGL 错误
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error: $error")
        }
    }


    fun setRotationAngle(angle: Float, isFrontCamera: Boolean = false) {
        mProgramManager.setRotationAngle(angle, isFrontCamera)
    }

    fun pauseRendering() {
        mIsRenderingPaused = true
    }

    fun resumeRendering() {
        mIsRenderingPaused = false
    }

    fun setProgramType(type: FilterType) {
        mProgramManager.setFilterType(type)
    }

}

package com.project.pixenchant.camera2.renderer.imp

import android.graphics.SurfaceTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

interface IBaseFilter {

    abstract fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onTextureIdAvailable: ((Int) -> Unit)?
    )
    abstract fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)
    abstract fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?)

    fun setUpUniforms(rotationMatrix: FloatArray)
    fun setRotationAngle(angle: Float, frontCamera: Boolean)
}


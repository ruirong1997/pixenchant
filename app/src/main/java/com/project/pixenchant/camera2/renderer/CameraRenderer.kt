package com.project.pixenchant.camera2.renderer

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.manager.ProgramManager
import com.project.pixenchant.ext.createFloatBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRenderer : GLSurfaceView.Renderer {

    companion object {
        private val TAG = this::class.java.name
    }

    var mOnSurfaceTextureAvailable: ((SurfaceTexture) -> Unit)? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mTextureId: Int = 0
    private var mProgram = 0
    private var mRotationMatrix = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
    private var mRotationAngle: Float = 0f
    private var mIsRenderingPaused = false
    private var mProgramManager = ProgramManager()

    private var mWidth = 0
    private var mHeight = 0


    private val mVertexBuffer = createFloatBuffer(
        floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
        )
    )

    private val mTexCoordBuffer = createFloatBuffer(
        floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        )
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        mTextureId = createTexture()

        mSurfaceTexture = SurfaceTexture(mTextureId)
        mSurfaceTexture?.let { mOnSurfaceTextureAvailable?.invoke(it) }
    }

    fun setRotationAngle(angle: Float, isFrontCamera: Boolean = false) {
        if (mRotationAngle == angle) return
        Log.d(TAG, "setRotationAngle: $angle")
        mRotationAngle = angle

        Matrix.setIdentityM(mRotationMatrix, 0)
        Matrix.rotateM(mRotationMatrix, 0, -mRotationAngle, 0f, 0f, 1f)
        if (isFrontCamera) {
            Matrix.scaleM(mRotationMatrix, 0, 1f, -1f, 1f)
        }
    }

    fun pauseRendering() {
        mIsRenderingPaused = true
    }

    fun resumeRendering() {
        mIsRenderingPaused = false
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        setSize(width, height)
        Log.d(TAG, "onSurfaceChanged: width=$width, height=$height")
    }

    override fun onDrawFrame(gl: GL10?) {
        if (mIsRenderingPaused) return

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        mSurfaceTexture?.updateTexImage()

        // 使用 ProgramManager 获取程序
        mProgram = mProgramManager.getProgram()
        GLES30.glUseProgram(mProgram)

        // 启用顶点属性
        enableVertexAttrib()

        mProgramManager.setUpUniforms(mRotationMatrix)

        //绑定纹理单元
        bindAndDrawTexture(GLES30.GL_TEXTURE0, mTextureId)

        //禁用顶点属性数组
        disableAttrib()

        // 检查 OpenGL 错误
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL error: $error")
        }
    }

    fun setProgramType(type: FilterType) {
        mProgramManager.setFilterType(type)
    }

    private fun enableVertexAttrib() {
        enableVertexAttrib(0, mVertexBuffer)
        enableVertexAttrib(1, mTexCoordBuffer)
    }

    private fun enableVertexAttrib(index: Int, buffer: FloatBuffer) {
        GLES30.glEnableVertexAttribArray(index)
        GLES30.glVertexAttribPointer(index, 2, GLES30.GL_FLOAT, false, 0, buffer)
    }

    private fun bindAndDrawTexture(textureUnit: Int, textureId: Int, textureTarget: Int = GL_TEXTURE_EXTERNAL_OES) {
        GLES30.glActiveTexture(textureUnit) // 激活指定的纹理单元
        GLES30.glBindTexture(textureTarget, textureId) // 绑定指定目标的纹理
        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun disableAttrib(){
        // 禁用位置为 0 的顶点属性（顶点位置）
        GLES30.glDisableVertexAttribArray(0)
        // 禁用位置为 1 的顶点属性（顶点颜色或其他）
        GLES30.glDisableVertexAttribArray(1)
    }

    private fun createTexture(): Int {
        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        GLES30.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0])

        GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST.toFloat())
        GLES30.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())

        GLES30.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        return textures[0]
    }

    fun release() {
        mSurfaceTexture?.release()
        mSurfaceTexture = null
        mProgramManager.cleanupPrograms() // 清理程序资源
        GLES30.glDeleteTextures(1, intArrayOf(mTextureId), 0)
    }

    private fun setSize(width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }

}

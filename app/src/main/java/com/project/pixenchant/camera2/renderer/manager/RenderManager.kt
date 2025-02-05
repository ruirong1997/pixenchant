package com.project.pixenchant.camera2.renderer.manager

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.Image
import android.opengl.GLES30
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.utils.FilterProgramFactory
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.createProgram
import com.project.pixenchant.camera2.renderer.imp.IBaseFilter
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.LandmarkerListener
import com.project.pixenchant.utils.BitmapUtils
import java.nio.IntBuffer
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderManager @Inject constructor(
    private val mMediaPipeManager: MediaPipeManager,
) : LandmarkerListener {

    private var mWidth = 0
    private var mHeight = 0
    private var mBuffer = IntArray(0)
    private var mTempBuffer = IntArray(0)

    private val loadedPrograms = mutableMapOf<String, Int>()
    private var mCurFilter = FilterType.THIN_FACE
    private var currentFilterProgram: IBaseFilter = FilterProgramFactory.create(mCurFilter, this)

    private var mListener: LandmarkerListener? = null

    fun isVideoStream(): Boolean {
        return when (mCurFilter) {
            //滤镜
            FilterType.NONE -> true
            FilterType.FANTASY -> true
            FilterType.BLUR -> false

            //人脸
            FilterType.THIN_FACE -> true
        }
    }

    fun setMarkerListener(listener: LandmarkerListener) {
        mListener = listener
    }

    fun setFilterType(filterType: FilterType) {
        mCurFilter = filterType
        currentFilterProgram = FilterProgramFactory.create(mCurFilter, this)
    }

    fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onSurfaceTextureAvailable: ((Int) -> Unit)?,
    ) {
        currentFilterProgram.onSurfaceCreated(gl, config, onSurfaceTextureAvailable)

        mMediaPipeManager.initialize()
        mMediaPipeManager.setMarkerListener(this)
        mMediaPipeManager.startMarker()
    }

    fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        initRenderBuffer()
        currentFilterProgram.onSurfaceChanged(gl, width, height)
    }

    fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?, originBitmap: Bitmap?) {
        if (isVideoStream()) {
            mMediaPipeManager.detectLiveStream(originBitmap)
        }
        currentFilterProgram.onDrawFrame(gl, surfaceTexture)
    }

    fun getProgram(): Int {
        return 0
    }

    fun setUpUniforms(rotationMatrix: FloatArray) {
        currentFilterProgram.setUpUniforms(rotationMatrix)
    }

    fun getProgram(vertexResId: Int, fragmentResId: Int): Int {
        val programKey = "$vertexResId-$fragmentResId"
        return loadedPrograms[programKey] ?: run {
            val program = createProgram(vertexResId, fragmentResId)
            loadedPrograms[programKey] = program
            program
        }
    }

    fun cleanupPrograms() {
        loadedPrograms.values.forEach { GLES30.glDeleteProgram(it) }
        loadedPrograms.clear()
    }

    fun setRotationAngle(angle: Float, frontCamera: Boolean) {
        currentFilterProgram.setRotationAngle(angle, frontCamera)
    }

    private fun initRenderBuffer() {
        mBuffer = IntArray(mWidth * mHeight)
        mTempBuffer = IntArray(mWidth * mHeight)
    }

    // 提取帧数据 频繁调用
    private fun getFrameBitmap(): Bitmap {
        // 读取帧缓冲区的像素数据
        GLES30.glReadPixels(
            0,
            0,
            mWidth,
            mHeight,
            GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE,
            IntBuffer.wrap(mBuffer)
        )

        // 翻转图像（OpenGL 的坐标原点在左下角，Bitmap 在左上角）
        for (i in 0 until mHeight) {
            System.arraycopy(mBuffer, i * mWidth, mTempBuffer, (mHeight - i - 1) * mWidth, mWidth)
        }

        return Bitmap.createBitmap(mTempBuffer, mWidth, mHeight, Bitmap.Config.ARGB_8888)
    }

    fun pauseRendering() {
        mMediaPipeManager.stopMarker()
    }

    fun resumeRendering() {
        mMediaPipeManager.startMarker()
    }

    override fun onError(error: String, errorCode: Int) {
        mListener?.onError(error, errorCode)

    }

    override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
        mListener?.onResults(resultBundle)
    }

}

package com.project.pixenchant.camera2.renderer.filter.face



import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.util.Log
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.UniformType
import com.project.pixenchant.camera2.renderer.filter.BaseRendererFilter
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper
import com.project.pixenchant.camera2.renderer.manager.RenderManager
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.setUniform
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 瘦脸
 */
class ThinFaceProgram(private val renderManager: RenderManager) : BaseRendererFilter(),
    FaceLandmarkerHelper.LandmarkerListener {

    private companion object {
        const val MAX_DOTS = 512       // 最大支持点数
        const val DOT_RADIUS = 1f    // 点半径
    }

    private var results: FaceLandmarkerResult? = null
    private var imageWidth = 1
    private var imageHeight = 1
//    private var scaleFactor = 1f

    // 画面分辨率
    private var resolution = floatArrayOf(0f, 0f)

    private val mPoints = ArrayList<Pair<Float, Float>>()

    private var dotCount = MAX_DOTS

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onTextureIdAvailable: ((Int) -> Unit)?,
    ) {
        super.onSurfaceCreated(gl, config, onTextureIdAvailable)
        renderManager.setMarkerListener(this)
//        points.add(Pair(0.0f, 0.0f))
//        points.add(Pair(-0.5f, -0.5f))
//        points.add(Pair(0.5f, 0.5f))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        resolution = floatArrayOf(width.toFloat(), height.toFloat())
    }

    override fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?) {
        super.onDrawFrame(gl, surfaceTexture)
        surfaceTexture?.updateTexImage()

        GLES30.glUseProgram(mProgram)

        // 启用顶点属性
        enableVertexAttrib()
        setUpUniforms(getRotationMatrix())

        val textureId = mBufferManager.getTextureId()
        bindAndDrawTexture(GLES30.GL_TEXTURE0, textureId)

        updateFaceTargetDots()
        disableAttrib()
    }

    override fun getProgram(): Int {
        return renderManager.getProgram(
            R.raw.thin_face_vertex_shader,
            R.raw.thin_face_fragment_shader
        )
    }

    override fun setUpUniforms(rotationMatrix: FloatArray) {
        val program = getProgram()

        // 合并 uniform 设置
        setUniform(program, "uRotationMatrix", rotationMatrix, UniformType.MATRIX4FV)
        setUniform(program, "uTexture", 0, UniformType.INT)

    }

    override fun onError(error: String, errorCode: Int) {
        Log.d("faceMarkerHelper", "onError :${error} errorCode:$errorCode")
    }

    override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
        Log.d("faceMarkerHelper", "resultBundle inferenceTime:${resultBundle.inferenceTime}")
        resultBundle.apply {
            setResults(result, inputImageHeight, inputImageWidth, RunningMode.LIVE_STREAM)
        }
    }

    //-- 数据更新逻辑 --
    private fun updateFaceTargetDots() {
        mPoints.clear()
        results?.faceLandmarks()?.forEach { landmark ->
            landmark.forEachIndexed { _, normalizedLandmark ->
                // 坐标转换：归一化坐标 → 屏幕像素坐标（Y轴翻转）
                val x = normalizedLandmark.x() * mWidth
                val y = normalizedLandmark.y() * mHeight

                mPoints.add(convertToNDC(x, y))
            }
        }
        drawPoints(mPoints)
    }

    /**
     * 将屏幕像素坐标转换为归一化设备坐标（NDC）。
     *
     * @param x 屏幕像素 x 坐标
     * @param y 屏幕像素 y 坐标
     * @param screenWidth 屏幕宽度（像素）
     * @param screenHeight 屏幕高度（像素）
     * @return Pair(ndcX, ndcY) 转换后的归一化坐标，范围为 -1 到 1
     */
    fun convertToNDC(x: Float, y: Float, screenWidth: Int = mWidth, screenHeight: Int = mHeight): Pair<Float, Float> {
        val ndcX = (x - screenWidth / 2f) / (screenWidth / 2f)
        val ndcY = (screenHeight / 2f - y) / (screenHeight / 2f)
        return Pair(ndcX, ndcY)
    }


    fun setResults(
        faceLandmarkerResults: FaceLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE,
    ) {
        results = faceLandmarkerResults

//        scaleFactor = when (runningMode) {
//            RunningMode.IMAGE, RunningMode.VIDEO -> {
//                min(mWidth * 1f / imageWidth, mHeight * 1f / imageHeight)
//            }
//            RunningMode.LIVE_STREAM -> {
//                max(mWidth * 1f / imageWidth, mHeight * 1f / imageHeight)
//            }
//        }

//        updateScaleFactor(scaleFactor)
        updateImageDimensions(imageWidth, imageHeight)
    }

//    fun updateScaleFactor(scaleFactor: Float) {
//        this.scaleFactor = scaleFactor
//    }

    fun updateImageDimensions(imageWidth: Int, imageHeight: Int) {
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight
    }

    private fun drawPoints(pointList: ArrayList<Pair<Float, Float>>) {
        // 每个点由2个坐标组成 (x, y)
        val numPoints = pointList.size
        val pointCoords = FloatArray(numPoints * 2)

        // 将传入的点坐标转换到数组中
        for (i in 0 until numPoints) {
            pointCoords[i * 2] = pointList[i].first   // x 坐标
            pointCoords[i * 2 + 1] = pointList[i].second  // y 坐标
        }

        // 创建顶点缓冲区
        val vertexBuffer = ByteBuffer.allocateDirect(pointCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(pointCoords).position(0)

        // 获取着色器程序（确保你的着色器资源路径正确）
        val program = renderManager.getProgram(R.raw.points_vertex_shader, R.raw.points_fragment_shader)
        GLES30.glUseProgram(program)

        // 启用顶点属性数组 0，并将顶点数据传递给顶点着色器中的 aPosition 属性
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer)

        // 绘制所有点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, numPoints)

        // 禁用顶点属性
        GLES30.glDisableVertexAttribArray(0)
    }

}





package com.project.pixenchant.camera2.renderer.filter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.util.Log
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.UniformType
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.setUniform
import com.project.pixenchant.camera2.renderer.manager.ProgramManager
import com.project.pixenchant.ext.getAppContext
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlurFilterProgram(private val programManager: ProgramManager) : BaseRendererFilter() {

    private var mBitmap: Bitmap? = null

    private var mBlurRadius = 3.0f

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onTextureIdAvailable: ((Int) -> Unit)?
    ) {
        GLES30.glClearColor(0f, 0f, 0f, 1f) // 设置背景色为黑色
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT) // 清空颜色缓冲区
        GLES30.glEnable(GLES30.GL_TEXTURE_2D) // 开启纹理
        mBitmap = BitmapFactory.decodeResource(getAppContext().resources, R.drawable.img)?.apply {
            val textureId = createBitmapTexture(this, 32,32)
            mBufferManager.setTextureId(textureId)
            onTextureIdAvailable?.invoke(textureId)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        createFbo()
    }

    override fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?) {
        super.onDrawFrame(gl, surfaceTexture)

        GLES30.glUseProgram(mProgram)

        enableVertexAttrib()

        setUpUniforms(getRotationMatrix())
        // 获取 uniform 位置
        val uBlurRadiusLocation = GLES30.glGetUniformLocation(mProgram, "uBlurRadius")
        val isHorizontalLoc = GLES30.glGetUniformLocation(mProgram, "isHorizontal")
        val uTextureLoc = GLES30.glGetUniformLocation(mProgram, "uTexture")

        // 设置模糊半径
        GLES30.glUniform1f(uBlurRadiusLocation, mBlurRadius)

        val textureId = mBufferManager.getTextureId()
        val frameBufferIds = mBufferManager.getFboBufferIds()
        val fboTextures = mBufferManager.getFboTextures()

        // 进行水平和垂直模糊
        applyBlur(frameBufferIds[0], textureId, isHorizontalLoc, 0) // 水平模糊
        applyBlur(frameBufferIds[1], fboTextures[0], isHorizontalLoc, 1) // 垂直模糊

        // 绘制最终结果
        drawFinalResult(fboTextures[1], uTextureLoc)

        disableAttrib()
    }

    /**
     * 绑定帧缓冲并应用模糊效果
     * @param frameBufferId 帧缓冲 ID
     * @param textureId 输入纹理 ID
     * @param isHorizontalLoc 模糊方向 uniform 位置
     * @param direction 模糊方向，0 为水平，1 为垂直
     */
    private fun applyBlur(frameBufferId: Int, textureId: Int, isHorizontalLoc: Int, direction: Int) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId)
        GLES30.glUniform1i(isHorizontalLoc, direction) // 设置模糊方向
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId) // 绑定输入纹理
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4) // 绘制四边形
    }

    /**
     * 绘制最终结果到默认帧缓冲
     * @param textureId 结果纹理 ID
     * @param uTextureLoc 纹理 uniform 位置
     */
    private fun drawFinalResult(textureId: Int, uTextureLoc: Int) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0) // 绑定到默认帧缓冲
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId) // 使用模糊后的纹理
        GLES30.glUniform1i(uTextureLoc, 0) // 将纹理传递给着色器
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4) // 绘制四边形
    }

    override fun getProgram(): Int {
        return programManager.getProgram(R.raw.blur_vertex_shader, R.raw.blur_shader)
    }

    override fun setUpUniforms(rotationMatrix: FloatArray) {
        val widthOffset = 1.0 / mWidth
        val heightOffset = 1.0 / mHeight

        setUniform(mProgram, "uWidthOffset", widthOffset, UniformType.FLOAT)
        setUniform(mProgram, "uHeightOffset", heightOffset, UniformType.FLOAT)
        setUniform(mProgram, "uBlurRadius", mBlurRadius, UniformType.FLOAT)
    }

    private fun createFbo() {
        val fboIds = mBufferManager.getFboBufferIds()
        val fboTextureIds = mBufferManager.getFboTextures()

        val createNum = 2  //缓冲帧数量

        // 生成帧缓冲和纹理
        GLES30.glGenFramebuffers(createNum, fboIds, 0)
        GLES30.glGenTextures(createNum, fboTextureIds, 0)

        // 设置纹理和帧缓冲
        for (i in 0..<createNum) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTextureIds[i])

            // 设置纹理参数
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

            // 创建纹理
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mWidth, mHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)

            // 绑定帧缓冲
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboIds[i])
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fboTextureIds[i], 0)

            // 检查帧缓冲是否完整
            if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
                Log.e("MyRenderer", "Framebuffer $i is not complete")
            } else {
                Log.d("MyRenderer", "Framebuffer $i is complete")
            }
        }


        // 切换到默认帧缓冲
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }
}
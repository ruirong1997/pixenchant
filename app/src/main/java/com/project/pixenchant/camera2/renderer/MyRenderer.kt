import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import com.project.pixenchant.R
import com.project.pixenchant.camera2.renderer.ImageRender
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.createProgram
import com.project.pixenchant.ext.createFloatBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// OpenGL 渲染器
class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var textureId = -1
    private var shaderProgram = -1
    private var positionHandle = -1
    private var texCoordHandle = -1

    private val vertices: FloatBuffer = createFloatBuffer(
        floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
        )
    )

    private val texCoords: FloatBuffer = createFloatBuffer(
        floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        )
    )

    private var blurRadius = 10f
    private var sigma = 3.0
    private var frameBufferIds = IntArray(2)  // 用于存储两个帧缓冲
    private var textures = IntArray(2)  // 用于存储两个纹理

    fun setBlurRadius(blurRadius: Float) {
        this.blurRadius = blurRadius
    }

    fun setSigma(sigma: Double) {
        this.sigma = sigma
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d("MyRenderer", "onSurfaceCreated")
        GLES30.glClearColor(0f, 0f, 0f, 1f) // 设置背景色为黑色
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT) // 清空颜色缓冲区
        GLES30.glEnable(GLES30.GL_TEXTURE_2D) // 开启纹理

        // 加载纹理
        textureId = loadTexture(context)
        shaderProgram = createShaderProgram()
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d("MyRenderer", "onDrawFrame")

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT) // 清除屏幕

        GLES30.glUseProgram(shaderProgram)

        enableVertexAttrib(0, vertices)
        enableVertexAttrib(1, texCoords)

        val uBlurRadiusLocation = GLES30.glGetUniformLocation(shaderProgram, "uBlurRadius")
        GLES30.glUniform1f(uBlurRadiusLocation, blurRadius)

        val isHorizontalLoc = GLES30.glGetUniformLocation(shaderProgram, "isHorizontal")

        //0垂直 1 水平
       // 第一个帧缓冲（水平模糊）
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[0])
        GLES30.glUseProgram(shaderProgram)
        GLES30.glUniform1i(isHorizontalLoc, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        // 第二个帧缓冲（垂直模糊）
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[0])
        GLES30.glUseProgram(shaderProgram)
        GLES30.glUniform1i(isHorizontalLoc, 1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        // 绑定纹理并绘制最终结果
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0) // 绑定到默认帧缓冲
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]) // 使用第二个帧缓冲的纹理
        GLES30.glUniform1i(GLES30.glGetUniformLocation(shaderProgram, "uTexture"), 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4) // 绘制最终四边形
    }



    private fun enableVertexAttrib(index: Int, buffer: FloatBuffer) {
        GLES30.glEnableVertexAttribArray(index)
        GLES30.glVertexAttribPointer(index, 2, GLES30.GL_FLOAT, false, 0, buffer)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d("MyRenderer", "onSurfaceChanged")
        GLES30.glViewport(0, 0, width, height) // 设置视口
        mWidth = width
        mHeight = height

        // 创建帧缓冲
        createFrameBuffer()
    }

    private fun createFrameBuffer() {
        // 生成帧缓冲和纹理
        GLES30.glGenFramebuffers(2, frameBufferIds, 0)
        GLES30.glGenTextures(2, textures, 0)

        // 设置纹理和帧缓冲
        for (i in 0..1) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i])

            // 设置纹理参数
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

            // 创建纹理
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mWidth, mHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)

            // 绑定帧缓冲
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[i])
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textures[i], 0)

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

    private fun createShaderProgram(): Int {
        return createProgram(R.raw.blur_vertex_shader, R.raw.blur_shader)
    }

    private fun loadTexture(context: Context): Int {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, false)

        val textureHandle = IntArray(1)
        GLES30.glGenTextures(1, textureHandle, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, scaledBitmap, 0)

        return textureHandle[0]
    }
}

// GLSurfaceView 用来渲染 OpenGL 内容
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private var blurRadius = 10f
    private var sigma = 3.0
    val renderer = ImageRender()

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setBlurRadius(blurRadius: Float) {
//        renderer.setBlurRadius(blurRadius)
    }

    fun setSigma(sigma: Double) {
//        renderer.setSigma(sigma)
    }
}


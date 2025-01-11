package com.android.mycamera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20.*
import com.project.pixenchant.ext.getAppContext

class GLRender() : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "test0523-GLRender"
        private const val BYTES_PER_FLOAT = 4
        private const val VERTEX_ATTRIB_POSITION = "aPosVertex"
        private const val VERTEX_ATTRIB_TEXTURE_POSITION = "aTexVertex"
        private const val UNIFORM_TEXTURE = "s_texture"
        private const val UNIFORM_VMATRIX = "vMatrix"
    }

    private var mPreviewSize: Size = Size(1440,1080)
    private val mVertexCoord = floatArrayOf(
        -1f, -1f,  // 左下
        1f, -1f,   // 右下
        -1f, 1f,   // 左上
        1f, 1f     // 右上
    )

    // 纹理坐标（s,t）
    private val mTextureCoord = floatArrayOf(
        0.0f, 0.0f,  // 左下
        1.0f, 0.0f,  // 右下
        0.0f, 1.0f,  // 左上
        1.0f, 1.0f   // 右上
    )

    private val vMatrix = FloatArray(16)
    private var mVertexLocation = 0
    private var mTextureLocation = 0
    private var mUTextureLocation = 0
    private var mVMatrixLocation = 0
    private var mShaderProgram = 0

    // 接收相机数据的纹理
    private val mTextureId = IntArray(1)
    // 接收相机数据的 SurfaceTexture
    var mSurfaceTexture: SurfaceTexture? = null

    var onSurfaceTextureAvailable: ((SurfaceTexture) -> Unit)? = null

    // 初始化数据
    fun initData(size: Size) {
        mPreviewSize = size
    }

    // 向外提供 surfaceTexture 实例
    fun getSurfaceTexture(): SurfaceTexture? {
        return mSurfaceTexture
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.v(TAG, "onSurfaceCreated()")

        // 设置清除渲染时的颜色
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // 创建并连接程序
        mShaderProgram = createAndLinkProgram("texture_vertex_shader.glsl", "texture_fragtment_shader.glsl")
        if (mShaderProgram != 0) {
            glUseProgram(mShaderProgram)
        }

        // 初始化着色器中各变量属性
        initAttribLocation()
        // 初始化顶点数据
        initVertexAttrib()
        // 初始化纹理
        initTexture()
        mSurfaceTexture?.let { onSurfaceTextureAvailable?.invoke(it) }

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceChanged(): $width x $height")
        // glViewport(GLint x, GLint y, GLsizei width, GLsizei height)
        // x、y 是指距离左下角的位置，单位是像素
        // 如果不设置 width，height，它们的值是布局默认大小，渲染占满整个布局
        glViewport(0, 450, mPreviewSize?.width ?: 0, mPreviewSize?.height ?: 0)
    }

    override fun onDrawFrame(gl: GL10) {
        // Log.v(TAG,"onDrawFrame()")
        // surfaceTexture 获取新的纹理数据
        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.getTransformMatrix(vMatrix)

        glClear(GL_COLOR_BUFFER_BIT)

        // 允许顶点着色器中属性变量 aPosVertex 接收来自缓冲区的顶点数据
        glEnableVertexAttribArray(mVertexLocation)
        // 允许顶点着色器中属性变量 aTexVertex 接收来自缓冲区的纹理 UV 顶点数据
        glEnableVertexAttribArray(mTextureLocation)

        // 矩阵赋值
        glUniformMatrix4fv(mVMatrixLocation, 1, false, vMatrix, 0)

        // 开始绘制，绘制 mVertexCoord.length / 2 即 4 个点
        // GL_TRIANGLE_STRIP 和 GL_TRIANGLE_FAN 的绘制方式不同，需要注意
        glDrawArrays(GL_TRIANGLE_STRIP, 0, mVertexCoord.size / 2)

        // 禁止顶点数组的句柄
        glDisableVertexAttribArray(mVertexLocation)
        glDisableVertexAttribArray(mTextureLocation)
    }

    private fun initVertexAttrib() {
        val mVertexCoordBuffer = getFloatBuffer(mVertexCoord)
        // 把顶点数据缓冲区绑定到顶点着色器中接收顶点数据的属性变量 aPosVertex
        glVertexAttribPointer(mVertexLocation, 2, GL_FLOAT, false, 0, mVertexCoordBuffer)

        val mTextureCoordBuffer = getFloatBuffer(mTextureCoord)
        // 把 UV 顶点数据缓冲区绑定到顶点着色器中接收顶点数据的属性变量 aTexVertex
        glVertexAttribPointer(mTextureLocation, 2, GL_FLOAT, false, 0, mTextureCoordBuffer)
    }

    private fun initAttribLocation() {
        mVertexLocation = glGetAttribLocation(mShaderProgram, VERTEX_ATTRIB_POSITION)
        mTextureLocation = glGetAttribLocation(mShaderProgram, VERTEX_ATTRIB_TEXTURE_POSITION)
        mUTextureLocation = glGetUniformLocation(mShaderProgram, UNIFORM_TEXTURE)
        mVMatrixLocation = glGetUniformLocation(mShaderProgram, UNIFORM_VMATRIX)
    }

    private fun initTexture() {
        // 创建纹理对象
        glGenTextures(mTextureId.size, mTextureId, 0)
        // 使用纹理对象创建 surfaceTexture，提供给外部使用
        mSurfaceTexture = SurfaceTexture(mTextureId[0])
        // 激活纹理：默认 0 号纹理单元，一般最多能绑 16 个，视 GPU 而定
        glActiveTexture(GL_TEXTURE0)
        // 绑定纹理：将纹理放到当前单元的 GL_TEXTURE_BINDING_EXTERNAL_OES 目标对象中
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId[0])
        // 配置纹理：过滤方式
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_NEAREST.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())

        // 将片段着色器的采样器（纹理属性：s_texture）设置为 0 号单元
        glUniform1i(mUTextureLocation, 0)
    }

    private fun createAndLinkProgram(vertexShaderFN: String, fragShaderFN: String): Int {
        // 创建着色器程序
        val shaderProgram = glCreateProgram()
        if (shaderProgram == 0) {
            Log.e(TAG, "Failed to create mShaderProgram")
            return 0
        }

        // 获取顶点着色器对象
        val vertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(vertexShaderFN))
        if (vertexShader == 0) {
            Log.e(TAG, "Failed to load vertexShader")
            return 0
        }

        // 获取片段着色器对象
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(fragShaderFN))
        if (fragmentShader == 0) {
            Log.e(TAG, "Failed to load fragmentShader")
            return 0
        }

        // 绑定顶点着色器到着色器程序
        glAttachShader(shaderProgram, vertexShader)
        // 绑定片段着色器到着色器程序
        glAttachShader(shaderProgram, fragmentShader)

        // 链接着色器程序
        glLinkProgram(shaderProgram)
        // 检查着色器链接状态
        val linked = IntArray(1)
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            glDeleteProgram(shaderProgram)
            Log.e(TAG, "Failed to link shaderProgram")
            return 0
        }

        return shaderProgram
    }

    private fun loadShader(type: Int, shaderSource: String): Int {
        // 创建着色器对象
        val shader = glCreateShader(type)
        if (shader == 0) {
            return 0 // 创建失败
        }
        // 加载着色器源
        glShaderSource(shader, shaderSource)
        // 编译着色器对象
        glCompileShader(shader)
        // 检查编译状态
        val compiled = IntArray(1)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            // 编译失败，执行：打印日志、删除链接到着色器程序的着色器对象、返回错误值
            Log.e(TAG, glGetShaderInfoLog(shader))
            glDeleteShader(shader)
            return 0
        }

        return shader
    }

    private fun loadShaderSource(fileName: String): String {
        val builder = StringBuilder()
        try {
            val inputStream = getAppContext().assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                builder.append(line).append("\n")
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.toString()
    }

    private fun getFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }
}

package com.project.pixenchant.camera2.renderer.utils

import android.opengl.GLES30
import android.util.Log
import com.project.pixenchant.camera2.data.UniformType
import com.project.pixenchant.ext.getAppContext

object FilterUtils {

    private val TAG = this::class.java.name

    fun createProgram(vertexResId: Int, fragmentResId: Int): Int {
        val vertexCode = loadShaderFromRawResource(vertexResId)
        val fragmentCode = loadShaderFromRawResource(fragmentResId)
        return createProgram(vertexCode, fragmentCode)
    }

    private fun createProgram(vertexCode: String, fragmentCode: String): Int {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentCode)
        val program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        checkLinkStatus(program)
        return program
    }

    private fun checkLinkStatus(program: Int) {
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val errorLog = GLES30.glGetProgramInfoLog(program)
            Log.e(TAG, "Program linking failed: $errorLog")
            GLES30.glDeleteProgram(program)
            throw RuntimeException("Program linking failed")
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        checkCompileStatus(shader)
        return shader
    }

    private fun checkCompileStatus(shader: Int) {
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorLog = GLES30.glGetShaderInfoLog(shader)
            Log.e(TAG, "Shader compilation failed: $errorLog")
            GLES30.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed")
        }
    }

   fun loadShaderFromRawResource(resId: Int): String {
        return getAppContext().resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }


    // 传参给着色器程序
   fun setUniform(program: Int, name: String, value: Any, type: UniformType) {
        val location = GLES30.glGetUniformLocation(program, name)

        // 如果 uniform 不存在，直接返回
        if (location == -1) return

        when (type) {
            UniformType.MATRIX4FV -> {
                if (value is FloatArray && value.size == 16) {
                    GLES30.glUniformMatrix4fv(location, 1, false, value, 0)
                }
            }

            UniformType.INT -> {
                if (value is Int) {
                    GLES30.glUniform1i(location, value)
                }
            }

            UniformType.FLOAT -> {
                if (value is Float) {
                    GLES30.glUniform1f(location, value)
                }
            }
        }
    }
}
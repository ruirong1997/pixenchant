package com.project.pixenchant.camera2.renderer.filter

import android.graphics.SurfaceTexture
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES30
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.UniformType
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.setUniform
import com.project.pixenchant.camera2.renderer.manager.RenderManager
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NoneFilterProgram(private val renderManager: RenderManager) : BaseRendererFilter() {


    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onTextureIdAvailable: ((Int) -> Unit)?
    ){
        super.onSurfaceCreated(gl, config, onTextureIdAvailable)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
    }


    override fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?) {
        super.onDrawFrame(gl, surfaceTexture)

        GLES30.glUseProgram(mProgram)

        // 启用顶点属性
        enableVertexAttrib()

        setUpUniforms(getRotationMatrix())

        val textureId = mBufferManager.getTextureId()
        bindAndDrawTexture(GLES30.GL_TEXTURE0, textureId)

        disableAttrib()
    }

    override fun getProgram(): Int {
        return renderManager.getProgram(R.raw.none_vertex_shader, R.raw.none_fragment_shader)
    }

    override fun setUpUniforms(rotationMatrix: FloatArray) {
        val program = getProgram()
        setUniform(program,"uRotationMatrix", rotationMatrix, UniformType.MATRIX4FV)
        setUniform(program,"uTexture", 0, UniformType.INT)
    }

}





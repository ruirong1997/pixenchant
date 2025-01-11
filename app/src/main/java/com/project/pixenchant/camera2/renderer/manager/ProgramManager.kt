package com.project.pixenchant.camera2.renderer.manager

import android.graphics.SurfaceTexture
import android.opengl.GLES30
import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.utils.FilterProgramFactory
import com.project.pixenchant.camera2.renderer.utils.FilterUtils.createProgram
import com.project.pixenchant.camera2.renderer.imp.IBaseFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ProgramManager {

    private val loadedPrograms = mutableMapOf<String, Int>()
    private var currentFilterProgram: IBaseFilter = FilterProgramFactory.create(FilterType.FANTASY, this)

    fun setFilterType(filterType: FilterType) {
        currentFilterProgram = FilterProgramFactory.create(filterType, this)
    }

    fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?,
        onSurfaceTextureAvailable: ((Int) -> Unit)?
    ) {
        currentFilterProgram.onSurfaceCreated(gl, config, onSurfaceTextureAvailable)
    }

    fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        currentFilterProgram.onSurfaceChanged(gl, width, height)
    }

    fun onDrawFrame(gl: GL10?, surfaceTexture: SurfaceTexture?) {
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

}

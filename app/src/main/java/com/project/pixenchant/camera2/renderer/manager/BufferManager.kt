package com.project.pixenchant.camera2.renderer.manager

import androidx.camera.core.processing.SurfaceProcessorNode.In

class BufferManager {

    //FBO
    private var mFboBuffer: IntArray = IntArray(2)
    private var mFboTextureIds: IntArray = IntArray(2)
    private var mFboTextures: IntArray = IntArray(2)

    //显示
    private var mTextureId = 0

    companion object {
        val instance: BufferManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BufferManager()
        }
    }

    fun setTextureId(textureId: Int) {
        mTextureId = textureId
    }

    fun getTextureId(): Int {
        return mTextureId
    }



    fun setFboBuffer() {

    }

    fun getFboBuffer(): IntArray {
        return mFboBuffer
    }

    fun setFboBufferId() {

    }

    fun getFboBufferIds(): IntArray {
        return mFboTextureIds
    }

    fun getFboTextures(): IntArray {
        return mFboTextures
    }
}
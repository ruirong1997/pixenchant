package com.project.pixenchant.ext

import java.nio.FloatBuffer

fun createFloatBuffer(data: FloatArray): FloatBuffer =
    java.nio.ByteBuffer.allocateDirect(data.size * 4)
        .order(java.nio.ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(data).position(0) }
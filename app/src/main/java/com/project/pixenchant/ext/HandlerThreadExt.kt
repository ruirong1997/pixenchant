package com.project.pixenchant.ext

import android.os.HandlerThread
import android.util.Log

fun HandlerThread.joinSafely() {
    try {
        join()
    } catch (e: InterruptedException) {
        Log.e("Camera2Controller", "Background thread interrupted", e)
    }
}
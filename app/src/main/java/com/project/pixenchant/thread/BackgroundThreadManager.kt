package com.project.pixenchant.thread

import android.os.Handler
import android.os.HandlerThread

class BackgroundThreadManager(private val threadName: String) {

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    // 启动线程
    fun startThread(listener: (() -> Unit)? = null)  {
        // 清空旧的线程和Handler
        backgroundThread?.quitSafely()
        backgroundThread = null
        backgroundHandler = null

        // 创建新的线程并启动
        backgroundThread = HandlerThread(threadName).apply {
            start()
        }

        // 初始化Handler
        backgroundHandler = Handler(backgroundThread!!.looper)

        // 确保线程已启动并通知外部
        listener?.invoke()
    }

    // 停止线程
    fun stopThread() {
        backgroundThread?.let {
            // 安全退出线程
            it.quitSafely()
            try {
                it.join() // 确保线程彻底停止
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                backgroundThread = null
                backgroundHandler = null
            }
        }
    }

    // 获取Handler，如果没有线程或线程已停止，则重新启动线程
    fun getHandler(): Handler {
        // 如果没有线程或线程已停止，则重新启动线程
        if (backgroundHandler == null || !backgroundThread!!.isAlive) {
            startThread()
        }
        return backgroundHandler ?: throw IllegalStateException("Handler is not initialized properly.")
    }

}

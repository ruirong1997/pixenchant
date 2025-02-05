package com.project.pixenchant.camera2.renderer.manager

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.Companion.DEFAULT_FACE_DETECTION_CONFIDENCE
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.Companion.DEFAULT_FACE_PRESENCE_CONFIDENCE
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.Companion.DEFAULT_FACE_TRACKING_CONFIDENCE
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.Companion.DEFAULT_NUM_FACES
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.Companion.DELEGATE_CPU
import com.project.pixenchant.camera2.renderer.manager.FaceLandmarkerHelper.LandmarkerListener
import com.project.pixenchant.ext.getAppContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPipeManager @Inject constructor(): FaceLandmarkerHelper.LandmarkerListener {

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    private lateinit var faceMarkerHelper: FaceLandmarkerHelper

    private var minFaceDetectionConfidence: Float = DEFAULT_FACE_DETECTION_CONFIDENCE
    private var minFaceTrackingConfidence: Float = DEFAULT_FACE_TRACKING_CONFIDENCE
    private var minFacePresenceConfidence: Float = DEFAULT_FACE_PRESENCE_CONFIDENCE
    private var maxNumFaces: Int = DEFAULT_NUM_FACES
    private var currentDelegate: Int = DELEGATE_CPU
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM

    private var mListener: LandmarkerListener? = null

    fun initialize() {
        backgroundExecutor = Executors.newSingleThreadExecutor()
        backgroundExecutor.execute {
            faceMarkerHelper = FaceLandmarkerHelper(
                context = getAppContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minFaceDetectionConfidence = minFaceDetectionConfidence,
                minFaceTrackingConfidence = minFaceTrackingConfidence,
                minFacePresenceConfidence = minFacePresenceConfidence,
                maxNumFaces = maxNumFaces,
                currentDelegate = currentDelegate,
                faceLandmarkerHelperListener = this
            )
        }
    }

    fun setMarkerListener(listener: LandmarkerListener) {
        mListener = listener
    }

    fun startMarker() {
        backgroundExecutor.execute {
            if (faceMarkerHelper.isClose()) {
                faceMarkerHelper.setupFaceLandmarker()
            }
        }
    }

    fun stopMarker() {
        if(this::faceMarkerHelper.isInitialized) {
            backgroundExecutor.execute { faceMarkerHelper.clearFaceLandmarker() }
        }
    }

    fun release() {
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    @SuppressLint("VisibleForTests")
    fun detectLiveStream(frameBitmap: Bitmap?) {
        frameBitmap ?: return
        if (!::faceMarkerHelper.isInitialized) return

        val frameTime = SystemClock.uptimeMillis()
        val mpImage = BitmapImageBuilder(frameBitmap).build()
        Log.d("faceMarkerHelper", "detectAsync")

        faceMarkerHelper.detectAsync(mpImage, frameTime)
    }

    override fun onError(error: String, errorCode: Int) {
        mListener?.onError(error, errorCode)
    }

    override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
        mListener?.onResults(resultBundle)
   }
}
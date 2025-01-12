import android.content.Context
import android.opengl.GLSurfaceView
import com.project.pixenchant.camera2.renderer.CameraRenderer

// GLSurfaceView 用来渲染 OpenGL 内容
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private var blurRadius = 10f
    private var sigma = 3.0
    val renderer = CameraRenderer()

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


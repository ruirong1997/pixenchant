import android.content.Context
import android.os.Build
import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView

object StatusBarUtil {

    /**
     * 获取状态栏的高度，适应不同版本
     */
    @Composable
    fun getStatusBarHeight(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 对于 Android 11 及以上版本，使用 WindowInsets 获取状态栏高度
            getStatusBarHeightForApiRAndAbove(context)
        } else {
            // 对于 Android 10 以下版本，使用资源文件获取状态栏高度
            getStatusBarHeightForLegacy(context)
        }
    }

    /**
     * 对于 Android 11 及以上版本，使用 WindowInsets 获取状态栏的高度
     */
    @Composable
    private fun getStatusBarHeightForApiRAndAbove(context: Context): Int {
        val windowInsets = LocalView.current.rootView.rootWindowInsets
        val insets = WindowInsetsCompat.toWindowInsetsCompat(windowInsets)
        return insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
    }

    /**
     * 对于 Android 10 以下版本，通过资源文件获取状态栏高度
     */
    private fun getStatusBarHeightForLegacy(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
}

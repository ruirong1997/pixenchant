package com.project.pixenchant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.project.pixenchant.ui.compose.MainScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            PixEnchantTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
                    initBackground()
                    MainScreen()
//                }
//            }
        }
    }

    private fun initBackground() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 获取 WindowInsetsController 来控制系统窗口外观
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        // 设置底部导航栏背景为黑色
        insetsController.isAppearanceLightNavigationBars = false // 使图标变白
        window.navigationBarColor = Color.Black.toArgb() // 设置导航栏背景颜色为黑色
    }

    override fun onResume() {
        super.onResume()
//        SystemUiUtils.hideSystemBars(this)
    }
}

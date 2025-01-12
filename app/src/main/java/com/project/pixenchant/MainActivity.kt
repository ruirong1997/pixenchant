package com.project.pixenchant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.project.pixenchant.ui.compose.MainScreen
import com.project.pixenchant.ui.compose.dialog.FilterDialog
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomBottomSheet() {
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // 屏幕内容
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show Bottom Sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { _ ->
        // 主界面内容
            if (showBottomSheet) {
                Box(modifier = Modifier.fillMaxSize()) {

                // 自定义底部弹窗
                CustomBottomSheetContent(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onDismissRequest = { showBottomSheet = false }
                )
            }
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun CustomBottomSheetContent(modifier: Modifier,onDismissRequest: () -> Unit) {
    val offsetY = remember { Animatable(1000f) }
    val coroutineScope = rememberCoroutineScope()

    // 动画：滑动效果
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(durationMillis = 500))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
            .padding(16.dp)
            .offset(y = Dp(offsetY.value)) // 添加动画效果
    ) {
        Column {
            Text("This is a custom bottom sheet", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    }
}

package com.project.pixenchant.ui.compose.camera2

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.project.pixenchant.R
import com.project.pixenchant.ext.noRippleSingleClick
import com.project.pixenchant.camera2.viewmodel.Camera2ViewModel
import com.project.pixenchant.ext.singleClick
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 右侧工具栏
 */
@Composable
fun ActionSideBar(cameraViewModel: Camera2ViewModel, modifier: Modifier = Modifier) {

    val openFilterDialog = remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp) // 设置所有项之间的间隔
    ) {
        ActionItem(R.drawable.ic_switch_camera, "翻转") { cameraViewModel.switchCamera() }
        ActionItem(R.drawable.ic_filter, "滤镜") {
            Log.d("openFilterDialog", "Click")
            openFilterDialog.value = true
        }
//        ActionItem(R.drawable.ic_switch_camera, "切换摄像头") { cameraViewModel.switchCamera() }

            // 通过回调获取关闭事件
        FilterDialog(
            openDialog = openFilterDialog.value,
            onDismiss = { openFilterDialog.value = false },
            onOpen = { openFilterDialog.value = true } // 外部控制打开弹窗
        )
    }
}

@Composable
fun ActionItem(resource: Int, text: String, onClick: () -> Unit) {

    val iconSize = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.icon_size).toDp()
    }

    // 使用 Column 来垂直排列图片和文字
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // 图片和文字居中
        modifier = Modifier
            .noRippleSingleClick {
                onClick.invoke()
            }
    ) {
        Image(
            painter = painterResource(id = resource),
            contentDescription = text,
            modifier = Modifier.size(iconSize)
        )

        // 显示文字
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium, // 可以调整字体样式
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp) // 图片与文字之间的间距
        )
    }
}


/**
 * 打开滤镜项目
 */
@Composable
fun FilterSelectDialog() {

    val bottomHeight = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.bottom_dialog_height).toDp()
    }

    val mediaTypes = listOf("Media 1", "Media 2", "Media 3") // 示例媒体类型列表
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { mediaTypes.size })
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .height(bottomHeight)
    ) {
        // TabRow，显示不同的标签
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(
                        currentTabPosition = tabPositions[pagerState.currentPage],
                        pageOffset = pagerState.currentPageOffsetFraction,
                    ),
                    color = Color.Black, // 设置指示器颜色
                    height = 2.dp // 设置指示器的高度
                )
            },
            divider = {} // 这里设置 divider 为空，去掉底部边框
        ) {
            mediaTypes.forEachIndexed { index, mediaType ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {}
                ) {
                    Text(
                        text = mediaType,
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .clickable {
                            }.noRippleSingleClick {
                                MainScope().launch {
                                    pagerState.scrollToPage(index)
                                }
                            }
                            .wrapContentSize(Alignment.Center),
                        style = TextStyle(
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                        )
                    )
                }
            }
        }

        // HorizontalPager 用于显示内容
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // 页面内容可以根据当前的 mediaType 来进行渲染
            Text(
                text = "Content for ${mediaTypes[page]}",
                modifier = Modifier.fillMaxSize(),
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}

@Composable
fun FilterDialog(
    openDialog: Boolean,  // 外部控制打开弹窗的状态
    onDismiss: () -> Unit, // 外部控制关闭弹窗的回调
    onOpen: () -> Unit = {} // 额外提供一个回调用于外部控制打开弹窗
) {
    val durationTime = 500
    // 显示 Dialog
    // Popup 显示内容
    Popup(
        onDismissRequest = {
            onDismiss() // 关闭时调用外部回调
        }
    ) {
        // 使用 AnimatedVisibility 来为 Popup 添加动画效果
        AnimatedVisibility(
            visible = openDialog,
            enter = slideInVertically(
                initialOffsetY = { it }, // 从底部弹出
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeIn(animationSpec = tween(durationMillis = durationTime)), // 渐现效果
            exit = slideOutVertically(
                targetOffsetY = { it }, // 向底部滑动消失
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeOut(animationSpec = tween(durationMillis = durationTime)) // 渐隐效果
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent) // 设置透明背景
                    .clickable {}
                    .noRippleSingleClick {
                        onDismiss() // 点击外部关闭对话框，调用外部回调
                    }
                    .wrapContentSize(Alignment.BottomCenter) // 从底部弹出
            ) {
                // Dialog 内容
                FilterSelectDialog()
            }
        }
    }
}

@Composable
fun MyPopupDemo() {
    var openDialog by remember { mutableStateOf(false) }

    val durationTime = 300 // 动画时长
    val onDismiss: () -> Unit = {
        // 外部回调，关闭时执行的操作
        println("Popup dismissed")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Button(onClick = { openDialog = true }) {
            Text("Open Dialog")
        }
    }
    // 点击按钮显示 Popup


    // Popup 显示内容
    Popup(
        onDismissRequest = {
            openDialog = false
            onDismiss() // 关闭时调用外部回调
        }
    ) {
        // 使用 AnimatedVisibility 来为 Popup 添加动画效果
        AnimatedVisibility(
            visible = openDialog,
            enter = slideInVertically(
                initialOffsetY = { it }, // 从底部弹出
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeIn(animationSpec = tween(durationMillis = durationTime)), // 渐现效果
            exit = slideOutVertically(
                targetOffsetY = { it }, // 向底部滑动消失
                animationSpec = tween(durationMillis = durationTime)
            ) + fadeOut(animationSpec = tween(durationMillis = durationTime)) // 渐隐效果
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent) // 设置透明背景
                    .clickable {

                    }
                    .noRippleSingleClick {
                        openDialog = false // 点击外部关闭对话框
                        onDismiss() // 调用外部回调
                    }
                    .wrapContentSize(Alignment.BottomCenter) // 从底部弹出
            ) {
                // Dialog 内容
                FilterSelectDialog()
            }
        }
    }
}
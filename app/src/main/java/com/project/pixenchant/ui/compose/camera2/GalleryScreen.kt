package com.project.pixenchant.ui.compose.camera2

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.MediaType
import com.project.pixenchant.camera2.viewmodel.Camera2ViewModel
import com.project.pixenchant.ext.noRippleSingleClick
import kotlinx.coroutines.delay
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

@Composable
fun MediaScreen(
    onDismiss: () -> Unit, // 外部关闭逻辑
    isVisibleExternally: Boolean = false, // 外部控制的显示/隐藏状态
    camera2ViewModel: Camera2ViewModel = hiltViewModel()
) {
    val density = LocalDensity.current.density
    val animationTime = 500
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // 控制动画显示状态
    var isVisible by remember { mutableStateOf(false) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // 监听外部isVisible状态变化，并触发动画
    LaunchedEffect(isVisibleExternally) {
        Log.d("Camera2ViewModel", "isVisibleExternally :$isVisibleExternally")
        isVisible = isVisibleExternally
        if (isVisibleExternally) {
            offsetY = 0f
            isVisible = true
            camera2ViewModel.syncMediaStorage() // 假设这是同步操作
        } else {
            isVisible = false
        }
    }

    if (isVisibleExternally) {
        Dialog(
            onDismissRequest = {
                Log.d("Camera2ViewModel", "onDismissRequest")
                isVisible = false // 开始播放退出动画
            },
            properties = DialogProperties(usePlatformDefaultWidth = false) // 自定义宽度
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // 半透明背景
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it }, // 从屏幕底部进入
                        animationSpec = tween(durationMillis = animationTime)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it }, // 向下退出
                        animationSpec = tween(durationMillis = animationTime)
                    )
                ) {
                    // 可拖拽的容器
                    Column(
                        modifier = Modifier
                            .offset { IntOffset(0, offsetY.roundToInt()) } // 使用偏移量来动态调整位置
                            .width(screenWidth)
                            .height(screenHeight)
                            .background(Color.White)
                    ) {
                        TitleScreen(
                            camera2ViewModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .draggable(
                                    orientation = Orientation.Vertical, // 垂直方向拖拽
                                    state = rememberDraggableState { delta ->
                                        offsetY = (offsetY + delta).coerceIn(
                                            -screenHeight.value * density,
                                            screenHeight.value * density
                                        )
                                    },
                                    onDragStopped = {
                                        if (offsetY > screenHeight.value * density / 2) {
                                            isVisible = false // 超过一半高度时关闭
                                        } else {
                                            offsetY = 0f // 否则恢复到原始位置
                                        }
                                    }
                                ),
                            onDismiss = onDismiss
                        )

                        MediaStorageScreen(camera2ViewModel)
                    }
                }
            }
        }
    }

    // 延迟关闭生命周期
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(animationTime.toLong()) // 等待退出动画完成
            if (isVisibleExternally) {
                onDismiss() // 触发外部关闭逻辑
            }
        }
    }
}


@Composable
fun TitleScreen(camera2ViewModel: Camera2ViewModel, modifier: Modifier, onDismiss: () -> Unit) {

    Box(modifier = modifier) {

        // 图片位于左边并且上下居中
        Image(
            painter = painterResource(id = R.drawable.ic_close), // 替换为你的图片资源
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterStart) // Align image to the left and vertically centered
                .padding(start = 8.dp)
                .clickable { }
                .noRippleSingleClick {
                    Log.d("MediaRepository", "onDismiss")
                    onDismiss()
                }
        )

        // 文本位于Box的中心
        Text(
            text = "所有照片",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold, // 设置字体加粗
            modifier = Modifier
                .align(Alignment.Center) // Align text to the center of the Box
        )
    }

}

@Composable
fun MediaStorageScreen(camera2ViewModel: Camera2ViewModel) {

    val mediaTypes = listOf(MediaType.ALL, MediaType.VIDEOS, MediaType.IMAGES)

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { mediaTypes.size })
    val showMediaType by camera2ViewModel.showMediaType.collectAsState(MediaType.CLOSE)

    var nextShowType by remember { mutableStateOf(mediaTypes.first()) }
    var isClick by remember { mutableStateOf(false) }

    // 同步媒体存储
    LaunchedEffect(Unit) {
        camera2ViewModel.syncMediaStorage()
        camera2ViewModel.openMediaStorage(mediaTypes.first())
    }


    val currentPage = pagerState.currentPage

    // 根据媒体类型切换页面
    LaunchedEffect(showMediaType) {
        val targetPage = when (showMediaType) {
            MediaType.ALL -> 0
            MediaType.VIDEOS -> 1
            MediaType.IMAGES -> 2
            else -> 0
        }
        pagerState.animateScrollToPage(targetPage)
    }

    LaunchedEffect(currentPage) {
        val curType = mediaTypes.getOrElse(currentPage) { MediaType.ALL }
        if (isClick) {
            if (showMediaType == curType) {
                nextShowType = curType
                val type = mediaTypes.getOrElse(currentPage) { MediaType.ALL }
                camera2ViewModel.openMediaStorage(type)
                isClick = false
            }
        } else {
            nextShowType = curType
            val type = mediaTypes.getOrElse(currentPage) { MediaType.ALL }
            camera2ViewModel.openMediaStorage(type)
        }
    }

    Column {
        // TabRow with dynamic indicator
        TabRow(
            selectedTabIndex = currentPage,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(
                        currentTabPosition = tabPositions[currentPage],
                        pageOffset = pagerState.currentPageOffsetFraction,
                    ),
                    color = Color.Black, // 设置指示器颜色
                    height = 2.dp // 设置指示器的高度
                )
            },
            divider = {} // 这里设置 divider 为空，去掉底部边框
        ) {
            mediaTypes.forEachIndexed { index, mediaType ->
                Text(
                    text = getTitle(mediaType),
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable { camera2ViewModel.openMediaStorage(mediaTypes[index]) }
                        .noRippleSingleClick {
                            isClick = true
                            camera2ViewModel.openMediaStorage(mediaTypes[index])
                        }
                        .wrapContentSize(Alignment.Center), // 使文字在容器内居中
                    style = TextStyle(
                        fontWeight = if (nextShowType == mediaTypes[index]) FontWeight.Bold else FontWeight.Normal,
                        color = if (nextShowType == mediaTypes[index]) Color.Black else Color.Gray
                    )
                )
            }
        }

        // HorizontalPager for displaying content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            key(mediaTypes[page]) {
                MediaDataScreenForType(
                    camera2ViewModel = camera2ViewModel,
                    mediaType = mediaTypes[page],
                    onMediaSelected = {}
                )
            }
        }
    }
}

private fun getTitle(type: MediaType): String {
    return when (type) {
        MediaType.ALL -> "全部"
        MediaType.VIDEOS -> "视频"
        MediaType.IMAGES -> "图片"
        else -> ""
    }
}

@Composable
fun MediaDataScreenForType(
    camera2ViewModel: Camera2ViewModel,
    mediaType: MediaType,
    onMediaSelected: (List<String>) -> Unit
) {
    MediaDataScreen(
        camera2ViewModel,
        mediaType,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 2.dp),
        onMediaSelected = onMediaSelected
    )
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MediaDataScreen(
    camera2ViewModel: Camera2ViewModel,
    mediaType: MediaType,
    modifier: Modifier,
    onMediaSelected: (List<String>) -> Unit
) {
    var selectedMedia by remember { mutableStateOf(ArrayList<String>()) }

    val mediaList by when (mediaType) {
        MediaType.VIDEOS -> camera2ViewModel.videoList.collectAsState()
        MediaType.IMAGES -> camera2ViewModel.imageList.collectAsState()
        MediaType.ALL -> camera2ViewModel.mediaList.collectAsState()
        else -> camera2ViewModel.mediaList.collectAsState()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(mediaList.size) { index ->
            val mediaItem = mediaList.get(index)// 获取当前图片的 URI
            val isSelected = mediaItem.toString() in selectedMedia // 判断是否已选中

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        selectedMedia = ArrayList(selectedMedia).apply {
                            if (isSelected) remove(mediaItem.toString()) else add(mediaItem.toString())
                        }
                        onMediaSelected(selectedMedia.toList()) // 通知选中状态变更
                    }
            ) {
                // 加载图片
                MediaItem(
                    mediaUri = mediaItem.uriPath,
                    onClick = { onMediaSelected(selectedMedia.toList()) }
                )

                // 如果选中，显示标记
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Red, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// 显示单个媒体项（图片或视频）
@Composable
fun MediaItem(mediaUri: Uri, onClick: () -> Unit) {
    key(mediaUri) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            factory = { context ->
                ImageView(context).apply {
                    Log.d("MediaItem", "mediaUri :$mediaUri")
                    scaleType = ImageView.ScaleType.FIT_XY  // 设置图片缩放模式为平铺
                    Glide.with(context)
                        .load(mediaUri)
                        .into(this)  // 直接加载图片到 ImageView
                }
            },
        )
    }
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    pageOffset: Float
): Modifier = composed(
    inspectorInfo =
    debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    // 动态计算宽度和偏移量
    val indicatorWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left + currentTabPosition.width * pageOffset,
    )

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
        .width(indicatorWidth)
}





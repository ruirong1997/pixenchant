package com.project.pixenchant.ui.compose.camera2.bottom

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.project.pixenchant.R
import com.project.pixenchant.camera2.data.MediaType
import com.project.pixenchant.camera2.viewmodel.Camera2ViewModel
import kotlin.math.roundToInt

@Composable
@SuppressLint("UseOfNonLambdaOffsetOverload")
fun BottomCameraControls(camera2ViewModel: Camera2ViewModel, modifier: Modifier) {

    val spaceSize = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.space_size).toDp()
    }

    val bottomHeight = with(LocalDensity.current) {
        LocalContext.current.resources.getDimension(R.dimen.bottom_dialog_height).toDp()
    }

    LaunchedEffect(Unit) {
        camera2ViewModel.syncMediaStorage()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(bottomHeight),
        ) {
        //选择模式
        CameraModeSelector(camera2ViewModel)

        // 相机模式按钮，居中
        Box(
            modifier = Modifier.align(Alignment.Center) // 使用 Box 将 CameraModeButton 居中
        ) {

            CameraMidLayout(camera2ViewModel)
            //相册

        }
    }
}

@Composable
fun CameraMidLayout(cameraViewModel: Camera2ViewModel) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // 定义引用
        val (cameraModeButton, galleryButton) = createRefs()

        // 中间相机模式按钮
        CameraModeButton(
            cameraViewModel,
            modifier = Modifier
                .constrainAs(cameraModeButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent) // 水平居中
            }
        )

        PhotoGalleryButton(
            modifier = Modifier.constrainAs(galleryButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(cameraModeButton.end)
                end.linkTo(parent.end)
            }
        )

    }

}
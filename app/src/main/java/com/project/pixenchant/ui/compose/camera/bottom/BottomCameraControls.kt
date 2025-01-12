package com.project.pixenchant.ui.compose.camera.bottom

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.constraintlayout.compose.ConstraintLayout
import com.project.pixenchant.R
import com.project.pixenchant.viewmodel.Camera2ViewModel

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(bottomHeight),
        ) {
        //选择模式
        CameraModeSelector(camera2ViewModel)

        // 相机模式按钮，居中
        Box(
            modifier = Modifier.fillMaxSize() // 使用 Box 将 CameraModeButton 居中
        ) {

            CameraMidLayout(
                camera2ViewModel,
                modifier = Modifier.fillMaxSize().align(Alignment.Center))
            //相册
        }
    }
}

@Composable
fun CameraMidLayout(cameraViewModel: Camera2ViewModel, modifier: Modifier) {

    ConstraintLayout(
        modifier = modifier
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
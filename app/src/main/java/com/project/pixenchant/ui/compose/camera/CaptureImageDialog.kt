package com.project.pixenchant.ui.compose.camera

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide

@Composable
fun CaptureImageDialog(bitmap: Bitmap?, onDismiss: () -> Unit) {
    // 如果没有图像，直接返回
    bitmap?.apply {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { },
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY  // 设置图片缩放模式为平铺
                        Glide.with(context)
                            .load(bitmap)
                            .into(this)  // 直接加载图片到 ImageView
                    }
                },
            )
        }
    }
}
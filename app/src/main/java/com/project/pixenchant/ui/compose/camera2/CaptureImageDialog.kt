package com.project.pixenchant.ui.compose.camera2

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide
import com.project.pixenchant.R

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
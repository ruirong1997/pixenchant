package com.project.pixenchant.ui.compose.dialog

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.pixenchant.R
import com.project.pixenchant.data.ItemData
import com.project.pixenchant.data.ItemType
import com.project.pixenchant.data.createItemDataList
import com.project.pixenchant.ext.getAppContext
import com.project.pixenchant.ext.pxToDp
import com.project.pixenchant.ui.view.HorizontalProgressBar
import com.project.pixenchant.ui.view.NoRippleInteractionSource
import com.project.pixenchant.utils.TabRowUtils.initTabMinWidthHacking
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun FaceDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(openDialog) {
        showBottomSheet = openDialog
    }

    if (showBottomSheet) {
        initTabMinWidthHacking()
        BottomDialog(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent),
            content = { FaceSelectDialog() },
            onDismissRequest = onDismiss
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload", "UnrememberedMutableState")
@Composable
fun FaceSelectDialog() {
    val mContext = LocalContext.current
    val mBottomHeight = with(LocalDensity.current) {
        mContext.resources.getDimension(R.dimen.bottom_face_dialog_height).toDp()
    }
    var mProgress by remember { mutableFloatStateOf(0f) }
    val mFaceArray = mContext.resources.getStringArray(R.array.face_array)

    var mSelectedFilterItem by remember { mutableStateOf("") }

    Column {
        HorizontalProgressBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, bottom = 16.dp),
            progress = mProgress, // 传递当前进度
            normalProgress = 50f,
            onProgressChanged = { newProgress -> mProgress = newProgress }, // 更新进度的回调
        )

        Box(
            modifier = Modifier
                .height(mBottomHeight)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .height(mBottomHeight)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "美颜",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // 水平居中对齐
                        .padding(top = 8.dp) // 添加顶部内边距
                )

                Spacer(modifier = Modifier.height(32.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mFaceArray.size) { index ->
                        val item = mFaceArray[index]
                        FaceItem(
                            data = item,
                            isSelected = mSelectedFilterItem == item,
                            onClick = { mSelectedFilterItem = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FaceItem(data: String, isSelected: Boolean, onClick: () -> Unit) {
    val mBorderColor = if (isSelected) Color.Red else Color.Transparent
    val mItemSize = 56.dp


    Column(
        modifier = Modifier
            .wrapContentSize()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(mItemSize)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(2.dp, mBorderColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data,
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(2.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) Color.Red else Color.White,
            textAlign = TextAlign.Center
        )
    }

}


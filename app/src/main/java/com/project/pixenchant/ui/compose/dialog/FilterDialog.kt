package com.project.pixenchant.ui.compose.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch


@Composable
fun FilterDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    // Sync internal state with external openDialog
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
            content = { FilterSelectDialog() },
            onDismissRequest = onDismiss
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload", "UnrememberedMutableState")
@Composable
fun FilterSelectDialog() {
    val mContext = LocalContext.current
    val mBottomHeight = with(LocalDensity.current) {
        mContext.resources.getDimension(R.dimen.bottom_filter_dialog_height).toDp()
    }

    val mFilterTitles = mContext.resources.getStringArray(R.array.filter_title_array)
    val mFilterList = rememberFilterList()
    val mFilterListState = rememberLazyListState()
    var mIsFilterScrolling by remember { mutableStateOf(false) }

    var mSelectedTabIndex by remember { mutableIntStateOf(0) }
    var mSelectedFilterItem by remember { mutableStateOf<String?>(null) }

    val mCoroutineScope = rememberCoroutineScope()

    var mProgress by remember { mutableFloatStateOf(0f) }

    fun getItemIndex(type: String): Int {
        return when (type) {
            ItemType.ITEM_FEATURED -> 0
            ItemType.ITEM_PORTRAIT -> 1
            ItemType.ITEM_DAILY -> 2
            ItemType.ITEM_VINTAGE -> 3
            ItemType.ITEM_FOOD -> 4
            ItemType.ITEM_SCENERY -> 5
            ItemType.ITEM_BLACK_WHITE -> 6
            else -> 0
        }
    }

    // 监听滑动状态
    LaunchedEffect(mFilterListState) {
        snapshotFlow { mFilterListState.isScrollInProgress }
            .collect { scrolling ->
                if (!scrolling && mIsFilterScrolling) {
                    // 滑动结束后的操作
                    val firstVisibleItemIndex = mFilterListState.firstVisibleItemIndex
                    // 获取第一个显示的 item
                    var firstVisibleItem = mFilterList[firstVisibleItemIndex]
                    if (firstVisibleItem.type == ItemType.ITEM_SPILT) {
                        firstVisibleItem = mFilterList[firstVisibleItemIndex + 1]
                    }
                    mSelectedTabIndex = getItemIndex(firstVisibleItem.type)
                }
                mIsFilterScrolling = scrolling
            }
    }

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
                FilterTabRow(
                    titles = mFilterTitles,
                    selectedTabIndex = mSelectedTabIndex,
                    onTabSelected = { tabIndex ->
                        mSelectedTabIndex = tabIndex
                        mCoroutineScope.launch {
                            mFilterListState.scrollToItem(
                                getFirstFilterIndex(
                                    mFilterTitles[tabIndex],
                                    mFilterList
                                )
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    state = mFilterListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mFilterList.size) { index ->
                        val item = mFilterList.get(index)
                        FilterItem(
                            data = item,
                            isSelected = mSelectedFilterItem == item.name,
                            onClick = { mSelectedFilterItem = item.name }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun FilterTabRow(
    titles: Array<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val mTextWidths =
        remember { mutableStateListOf<Float>().apply { addAll(List(titles.size) { 0f }) } }

    Row {
        Box(
            modifier = Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center // 使内容居中
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_prohibit),
                contentDescription = "Tab Icon",
                modifier = Modifier.size(18.dp) // 设置图片大小
            )
        }

        ScrollableTabRow(
            modifier = Modifier.padding(end = 16.dp),
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Black,
            contentColor = Color.White,
            edgePadding = 1.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .requiredWidth(mTextWidths[selectedTabIndex].dp)
                        .clip(RoundedCornerShape((mTextWidths[selectedTabIndex] / 2).dp)),
                    color = Color.White
                )
            },
            divider = {}
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .onGloballyPositioned { coordinates ->
                                    mTextWidths[index] = coordinates.size.width.toFloat().pxToDp
                                }
                        ) {

                            Text(
                                text = title,
                                style = TextStyle(fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    },
                    interactionSource = NoRippleInteractionSource()
                )
            }
        }
    }

}

@Composable
fun FilterItem(data: ItemData, isSelected: Boolean, onClick: () -> Unit) {
    val mBorderColor = if (isSelected) Color.Red else Color.Transparent
    val mItemSize = 56.dp

    if (data.type == ItemType.ITEM_SPILT) {
        Box(
            modifier = Modifier
                .width(mItemSize / 2)
                .height(mItemSize),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_split),
                contentDescription = "Split Image",
                modifier = Modifier
                    .width(mItemSize / 2)
                    .height(mItemSize)
            )
        }
    } else {
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
                    text = data.name,
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
                text = data.name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.Red else Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getFirstFilterIndex(type: String, filterList: List<ItemData>): Int {
    filterList.forEachIndexed { index, data ->
        if (type == data.type) {
            return index
        }
    }
    return 0
}

fun rememberFilterList(): List<ItemData> {
    val context = getAppContext()
    val mFeaturedListData = createItemDataList(
        ItemType.ITEM_FEATURED,
        context.resources.getStringArray(R.array.filter_featured_array)
    )
    val mPortraitListData = createItemDataList(
        ItemType.ITEM_PORTRAIT,
        context.resources.getStringArray(R.array.filter_portrait_array)
    )
    val mDailyListData = createItemDataList(
        ItemType.ITEM_DAILY,
        context.resources.getStringArray(R.array.filter_daily_array)
    )
    val mVintageListData = createItemDataList(
        ItemType.ITEM_VINTAGE,
        context.resources.getStringArray(R.array.filter_vintage_array)
    )
    val mFoodListData = createItemDataList(
        ItemType.ITEM_FOOD,
        context.resources.getStringArray(R.array.filter_food_array)
    )
    val mSceneryListData = createItemDataList(
        ItemType.ITEM_SCENERY,
        context.resources.getStringArray(R.array.filter_scenery_array)
    )
    val mBlackAndWhiteListData = createItemDataList(
        ItemType.ITEM_BLACK_WHITE,
        context.resources.getStringArray(R.array.filter_black_white_array)
    )

    val mSpiltData = listOf(ItemData(ItemType.ITEM_SPILT, ""))
    return listOf(
        mFeaturedListData, mSpiltData, mPortraitListData, mSpiltData,
        mDailyListData, mSpiltData, mVintageListData, mSpiltData,
        mFoodListData, mSpiltData, mSceneryListData, mSpiltData, mBlackAndWhiteListData
    ).flatten()
}
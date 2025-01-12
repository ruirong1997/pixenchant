package com.project.pixenchant.data

import com.project.pixenchant.R
import com.project.pixenchant.ext.getAppContext

data class ItemData(
    var type: String,
    val name: String,
    var value: Int = 80
)

object ItemType {
    const val ITEM_SPILT = "SPILT"

    val ITEM_FEATURED = getString(R.string.featured)
    val ITEM_PORTRAIT = getString(R.string.portrait)
    val ITEM_DAILY = getString(R.string.daily)
    val ITEM_VINTAGE = getString(R.string.vintage)
    val ITEM_FOOD = getString(R.string.food)
    val ITEM_SCENERY = getString(R.string.scenery)
    val ITEM_BLACK_WHITE = getString(R.string.black_white)
}

fun createItemDataList(type: String, titles: Array<String>): List<ItemData> {
    return titles.map { title ->
        ItemData(type, title)
    }
}

fun getString(resId: Int): String {
   return getAppContext().resources.getString(resId)
}
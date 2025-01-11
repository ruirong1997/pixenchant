package com.project.pixenchant.ext

import android.util.TypedValue
import android.content.res.Resources

// dp 转 px
val Float.dpToPx: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

// px 转 dp
val Float.pxToDp: Float
    get() = this / Resources.getSystem().displayMetrics.density

// sp 转 px
val Float.spToPx: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

// px 转 sp
val Float.pxToSp: Float
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

// Int 类型支持
val Int.dpToPx: Float
    get() = this.toFloat().dpToPx

val Int.pxToDp: Float
    get() = this.toFloat().pxToDp

val Int.spToPx: Float
    get() = this.toFloat().spToPx

val Int.pxToSp: Float
    get() = this.toFloat().pxToSp

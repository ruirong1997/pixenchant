package com.project.pixenchant.camera2.model

import com.project.pixenchant.camera2.data.DialogState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DialogControlRepository @Inject constructor() {

    /**
     * 底部相机控制栏
     */
    private val _showBottomControlDialog = MutableStateFlow(true)
    val showBottomControlDialog = _showBottomControlDialog

    /**
     * 右侧功能控制栏
     */
    private val _showSideBarDialog = MutableStateFlow(true)
    val showSideBarDialog = _showSideBarDialog

    /**
     * 滤镜弹窗
     */
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog = _showFilterDialog

    /**
     * 美颜
     */
    private val _showFaceDialog = MutableStateFlow(false)
    val showFaceDialog = _showFaceDialog


    fun showBottomControlDialog(isShow: Boolean) {
        _showBottomControlDialog.value = isShow
    }

    fun showFilterDialog(isShow: Boolean) {
        _showFilterDialog.value = isShow
    }

    fun showFaceDialog(isShow: Boolean) {
        _showFaceDialog.value = isShow
    }

    fun showSidebarDialog(isShow: Boolean) {
        _showSideBarDialog.value = isShow
    }
}

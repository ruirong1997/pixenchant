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
     * 滤镜弹窗
     */
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog = _showFilterDialog


    fun showBottomControlDialog(isShow: Boolean) {
        _showBottomControlDialog.value = isShow
    }

    fun showFilterDialog(isShow: Boolean) {
        _showFilterDialog.value = isShow
    }
}

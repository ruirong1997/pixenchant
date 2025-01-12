package com.project.pixenchant.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.pixenchant.camera2.model.DialogControlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    val dialogRepository: DialogControlRepository,
) : ViewModel() {

    val showBottomControlDialog = dialogRepository.showBottomControlDialog
    val showFilterDialog = dialogRepository.showFilterDialog

    fun showBottomControlDialog(isShow: Boolean) {
        dialogRepository.showBottomControlDialog(isShow)
    }

    fun showFilterDialog(isShow: Boolean) {
        Log.d("ActionSideBar", "showFilterDialog :$isShow")
        dialogRepository.showBottomControlDialog(!isShow)
        dialogRepository.showFilterDialog(isShow)
    }

}
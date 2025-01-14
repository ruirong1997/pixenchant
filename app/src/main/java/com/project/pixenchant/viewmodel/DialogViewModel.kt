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
    val showSidebarDialog = dialogRepository.showSideBarDialog
    val showFilterDialog = dialogRepository.showFilterDialog
    val showFaceDialog = dialogRepository.showFaceDialog

    fun showBottomControlDialog(isShow: Boolean) {
        dialogRepository.showBottomControlDialog(isShow)
    }

    fun showSidBarDialog(isShow: Boolean) {
        dialogRepository.showSidebarDialog(isShow)
    }

    fun showFilterDialog(isShow: Boolean) {
        Log.d("ActionSideBar", "showFilterDialog :$isShow")
        if (isShow) {
            dialogRepository.showBottomControlDialog(false)
            dialogRepository.showFaceDialog(false)
            dialogRepository.showSidebarDialog(false)
            dialogRepository.showFilterDialog(true)
        } else {
            dialogRepository.showFilterDialog(false)
            dialogRepository.showSidebarDialog(true)
            dialogRepository.showBottomControlDialog(true)
        }
    }

    fun showFaceDialog(isShow: Boolean) {
        Log.d("ActionSideBar", "showFilterDialog :$isShow")
        if (isShow) {
            dialogRepository.showBottomControlDialog(false)
            dialogRepository.showFilterDialog(false)
            dialogRepository.showSidebarDialog(false)
            dialogRepository.showFaceDialog(true)
        } else {
            dialogRepository.showFaceDialog(false)
            dialogRepository.showSidebarDialog(true)
            dialogRepository.showBottomControlDialog(true)
        }
    }


}
package com.project.pixenchant.camera2.renderer.utils

import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.filter.BlurFilterProgram
import com.project.pixenchant.camera2.renderer.filter.FantasyFilterProgram
import com.project.pixenchant.camera2.renderer.filter.NoneFilterProgram
import com.project.pixenchant.camera2.renderer.imp.IBaseFilter
import com.project.pixenchant.camera2.renderer.manager.ProgramManager

object FilterProgramFactory {

    fun create(filterType: FilterType, programManager: ProgramManager): IBaseFilter {
        return when (filterType) {
            FilterType.NONE -> NoneFilterProgram(programManager)
            FilterType.FANTASY -> FantasyFilterProgram(programManager)
            FilterType.BLUR -> BlurFilterProgram(programManager)
        }
    }
}
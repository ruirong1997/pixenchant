package com.project.pixenchant.camera2.renderer.utils

import com.project.pixenchant.camera2.data.FilterType
import com.project.pixenchant.camera2.renderer.filter.BlurFilterProgram
import com.project.pixenchant.camera2.renderer.filter.FantasyFilterProgram
import com.project.pixenchant.camera2.renderer.filter.NoneFilterProgram
import com.project.pixenchant.camera2.renderer.filter.face.ThinFaceProgram
import com.project.pixenchant.camera2.renderer.imp.IBaseFilter
import com.project.pixenchant.camera2.renderer.manager.RenderManager

object FilterProgramFactory {

    fun create(filterType: FilterType, renderManager: RenderManager): IBaseFilter {
        return when (filterType) {
            //滤镜
            FilterType.NONE -> NoneFilterProgram(renderManager)
            FilterType.FANTASY -> FantasyFilterProgram(renderManager)
            FilterType.BLUR -> BlurFilterProgram(renderManager)


            //人脸
            FilterType.THIN_FACE -> ThinFaceProgram(renderManager)
        }
    }
}
package com.project.pixenchant.camera2.data

/**
 * 滤镜类型
 */
enum class FilterType {
    NONE,    //无
    FANTASY, //奇幻
    BLUR,    //高斯模糊

    THIN_FACE, //瘦脸
}

/**
 * 流类型
 */
enum class StreamType {
    IMAGE_STREAM,   //图像流
    VIDEO_STREAM,   //视频流
    LIVE_STREAM,    //预览流
}

// Uniform 类型
enum class UniformType {
    MATRIX4FV,   // 4x4矩阵
    INT,         // 整数
    FLOAT        // 浮动数
}
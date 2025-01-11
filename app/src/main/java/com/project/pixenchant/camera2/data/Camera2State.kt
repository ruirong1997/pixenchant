package com.project.pixenchant.camera2.data

enum class Camera2State {
    INITIALIZING,   // 初始化中
    OPENING,
    OPENED,
    CLOSING,
    CLOSED,
    ERROR
}

enum class CameraMode {
    VIDEO,             // 录像模式
    PHOTO,             // 拍照模式
    BURST,             // 连拍模式
    TIMELAPSE,         // 延时摄影模式
    SEGMENTED_VIDEO    // 分段录像模式
}

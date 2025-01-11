package com.project.pixenchant.camera2.data


data class CameraState(
    var deviceState: CameraDeviceState = CameraDeviceState.IDLE,// 摄像头设备状态
    var sessionState: SessionState = SessionState.IDLE,    // 会话状态
    var requestState: RequestState = RequestState.IDLE     // 请求状态
)

/**
 * 表示相机设备的状态
 */
enum class CameraDeviceState {
    IDLE,
    ON_OPENED,        // 相机已打开
    ON_CLOSING,       // 相机关闭中
    ON_CLOSED,        // 相机已关闭
    ON_DISCONNECTED,  // 相机已断开
    ON_ERROR          // 相机发生错误
}


/**
 * 表示相机会话的状态
 */
enum class SessionState {
    IDLE,
    CONFIGURING,   // 正在配置会话
    ACTIVE,        // 会话激活中
    INACTIVE,      // 会话未激活
    CLOSED         // 会话已关闭
}

/**
 * 表示相机请求的状态
 */
enum class RequestState {
    IDLE,         //初始化
    QUEUED,       // 请求已加入队列
    SUBMITTED,    // 请求已提交
    COMPLETED,    // 请求已完成
    FAILED        // 请求失败
}

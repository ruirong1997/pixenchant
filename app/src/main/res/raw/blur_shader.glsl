#version 300 es
precision mediump float;

in vec2 vTexCoord;
out vec4 fragColor;

uniform sampler2D uTexture;
uniform float uWidthOffset;
uniform float uHeightOffset;
uniform float uBlurRadius; // 用来控制模糊的半径
uniform bool isHorizontal; // 用来区分是水平还是垂直模糊

// 高斯函数，用于生成内核权重
float gaussian(float x, float sigma) {
    return exp(-0.5 * (x * x) / (sigma * sigma)) / (sqrt(2.0 * 3.14159) * sigma);
}

void main() {
//    fragColor = texture(uTexture, vTexCoord);
    // 获取纹理的大小并转换为 vec2
//    vec2 texOffset = 1.0 / vec2(textureSize(uTexture, 0));  // 修正：转换为 vec2
    vec2 texOffset = vec2(1.0 / 32.0, 1.0 / 32.0);
    vec4 color = vec4(0.0);

    // 基于模糊半径计算 sigma
    float sigma = uBlurRadius / 2.0;

    // 动态生成内核并应用
    int kernelSize = int(uBlurRadius * 2.0 + 1.0); // 内核大小根据模糊半径动态计算

    // 确保 kernelSize 是奇数
    if (kernelSize % 2 == 0) {
        kernelSize++;  // 如果是偶数，+1，确保它是奇数
    }

    float totalWeight = 0.0;

    if (isHorizontal) {
        // 水平模糊
        for (int i = -kernelSize / 2; i <= kernelSize / 2; i++) {
            float weight = gaussian(float(i), sigma);
            color += texture(uTexture, vTexCoord + vec2(float(i) * texOffset.x, 0.0)) * weight;
            totalWeight += weight;

        }
    } else {
        // 垂直模糊
        for (int i = -kernelSize / 2; i <= kernelSize / 2; i++) {
            float weight = gaussian(float(i), sigma);
            color += texture(uTexture, vTexCoord + vec2(0.0, float(i) * texOffset.y)) * weight;
            totalWeight += weight;
        }
    }

    // 归一化结果
    fragColor = color / totalWeight;
}

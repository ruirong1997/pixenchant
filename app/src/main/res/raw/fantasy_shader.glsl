#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES uTexture;
uniform float uTime;

in vec2 vTexCoord;
out vec4 fragColor;

void main() {
    float time = mod(uTime, 6.28318530718); // 限制时间在 [0, 2π] 范围
    float wave = sin(vTexCoord.y * 8.0 + time) * 0.01; // 降低频率和幅度
    vec2 distortedCoord = vTexCoord + vec2(wave, 0.0);

    fragColor = texture(uTexture, distortedCoord);

//颜色变幻
//    float offset = 0.1 * sin(time * 0.5); // 减小偏移频率
//    color.r += offset;
//    color.b -= offset;
//
//    fragColor = color;
}

#version 300 es
precision mediump float;

// 顶点位置和纹理坐标
in vec4 aPosition;     // 顶点位置
in vec2 aTexCoord;     // 纹理坐标

out vec2 vTexCoord;    // 传递给片段着色器的纹理坐标

void main() {
    gl_Position = aPosition; // 计算顶点位置
    vTexCoord = aTexCoord;   // 将纹理坐标传递给片段着色器
}
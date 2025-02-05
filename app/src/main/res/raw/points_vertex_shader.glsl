#version 300 es
layout(location = 0) in vec4 aPosition;

void main() {
    gl_Position = aPosition;
    // 设置点大小（单位：像素），此值可根据需要调整
    gl_PointSize = 5.0;
}
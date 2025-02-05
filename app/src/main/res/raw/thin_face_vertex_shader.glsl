//uniform mat4 uRotationMatrix;
//attribute vec4 aPosition;
//attribute vec2 aTexCoord;
//varying vec2 vTexCoord;
//
//void main() {
//    // 通过旋转矩阵变换顶点位置
//    gl_Position = uRotationMatrix * aPosition;
//    vTexCoord = aTexCoord;
//}
#version 300 es

uniform mat4 uRotationMatrix;
in vec4 aPosition;
in vec2 aTexCoord;
out vec2 vTexCoord;

void main() {
    // 通过旋转矩阵变换顶点位置
    gl_Position = uRotationMatrix * aPosition;
    vTexCoord = aTexCoord;
}

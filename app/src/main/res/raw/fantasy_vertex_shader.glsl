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

//#extension GL_OES_EGL_image_external : require
//precision mediump float;
//varying vec2 vTexCoord;
//uniform samplerExternalOES uTexture;
//
//void main() {
//    gl_FragColor = texture2D(uTexture, vTexCoord);
//}

#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

in vec2 vTexCoord;
uniform samplerExternalOES uTexture;
out vec4 fragColor;

void main() {
    fragColor = texture(uTexture, vec2(vTexCoord.x, 1.0 - vTexCoord.y));
}


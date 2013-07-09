// GENERATED FILE //
#ifndef GL_ES
#define lowp
#define mediump
#define highp
#endif
varying vec2 v_texCoord0;

uniform mat4 u_modelViewProject;

attribute vec4 a_position;
attribute vec2 a_texCoord0;

void main() {
 v_texCoord0 = a_texCoord0;
 gl_Position = u_modelViewProject * a_position;
}


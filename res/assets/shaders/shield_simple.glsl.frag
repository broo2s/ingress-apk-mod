// GENERATED FILE //
#ifndef GL_ES
#define lowp
#define mediump
#define highp
#endif
#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_color;
uniform mat4 u_modelViewProject;

void main() {
 gl_FragColor = vec4(u_color.rgb, 0.25); 
}


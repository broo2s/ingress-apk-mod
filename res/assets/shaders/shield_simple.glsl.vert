// GENERATED FILE //
#ifndef GL_ES
#define lowp
#define mediump
#define highp
#endif
attribute vec2 a_texCoord0;
attribute vec3 a_position;
uniform mat4 u_modelViewProject;
void main ()
{
  vec4 tmpvar_1;
  tmpvar_1.w = 0.85;
  tmpvar_1.xyz = a_position;
  gl_Position = (u_modelViewProject * tmpvar_1);
}


// GENERATED FILE //
#ifndef GL_ES
#define lowp
#define mediump
#define highp
#endif
uniform mat4 u_modelViewProject;
uniform vec3 u_cameraPos;
attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute float a_scale;

uniform vec2 u_mapCenter;
uniform vec4 u_globParams[120];

attribute float a_speed;
attribute float a_portalIndex;
attribute float a_index;

const float NUM_PARTICLES = 3.0;
const float CAMSCALE_EXPONENT = 0.25;
const vec3 y_hat = vec3(0.0, 1.0, 0.0);

void main() {
 int portalIndex = int(a_portalIndex);
 float nOffset = u_globParams[portalIndex].w;
 if (a_index >= NUM_PARTICLES + nOffset || a_index < nOffset) {
   gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
 } else {
   vec3 position = vec3(u_globParams[portalIndex].x + u_mapCenter.x, 0.0, u_globParams[portalIndex].z + u_mapCenter.y);

   vec3 camVec = a_position - u_cameraPos;
   vec3 camVecNorm = normalize(camVec);
   float camScale = pow(length(camVec), CAMSCALE_EXPONENT);

   vec3 right = cross(camVecNorm, y_hat);
   vec3 up = cross(right, camVecNorm);
   vec2 scales = 0.5 * a_scale * (a_texCoord0.xy - vec2(0.5)) * camScale;
   right = right * scales.x;
   up = up * scales.y;

   gl_Position = u_modelViewProject * vec4(position + up + right, 1.0);
 }
}


#version 400 core

in vec3 pos;
in vec2 tex;

out vec2 pass_tex;

uniform vec2 panelSize;
uniform mat4 matrixMVP;

void main(){
    gl_Position = matrixMVP * vec4(pos, 1.0);
    pass_tex = vec2(gl_Position.z, gl_Position.w);
}
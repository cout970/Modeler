#version 400 core

in vec3 pos;
in vec2 tex;

out vec2 pass_tex;

uniform vec2 panelSize;
uniform mat4 matrixMVP;

void main(){
    vec3 pos_final = vec3(pos.x / panelSize.x * 1000, pos.y / panelSize.y * 1000, pos.z);
    gl_Position = matrixMVP * vec4(pos_final, 1.0);
    pass_tex = tex;
}
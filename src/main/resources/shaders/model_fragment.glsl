#version 400 core

in vec2 pass_tex;

out vec4 pixel;

uniform sampler2D sampler;
uniform vec3 color;

void main(){
//    vec4 te = texture(sampler, pass_tex);
//    if(te.w < 0.1) discard;
//    pixel = te;
    pixel = vec4(color, 1.0);
//    pixel = vec4(0.0, pass_tex.x, pass_tex.y, 1.0);
}
#version 400 core

in vec3 pass_color;
in vec2 pass_texture;

out vec4 out_color;

uniform float useColor;
uniform sampler2D textureSampler;

void main(void){

    if(useColor > 0.5) {
        out_color = vec4(pass_color, 1.0);
    } else {
        out_color = texture(textureSampler, pass_texture);
        if(out_color.w < 0.1) discard;
    }
}
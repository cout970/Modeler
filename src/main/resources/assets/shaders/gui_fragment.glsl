#version 400 core

in vec2 texCoords;

out vec4 out_color;

uniform sampler2D textureSampler;

void main(void){

    out_color = texture(textureSampler, texCoords);
    if(out_color.w < 0.1) discard;
}
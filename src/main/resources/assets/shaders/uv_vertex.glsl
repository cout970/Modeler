#version 400 core

//input vertex parameters
//vertex position
in vec3 in_position;
//vertex texture coordinates
in vec3 in_color;
in vec2 in_texture;

out vec3 pass_color;
out vec2 pass_texture;

uniform mat4 matrix;

void main(void){

    gl_Position = matrix * vec4(in_position.xyz, 1.0);

    //values passed to openGL to interpoalte
    pass_color = in_color;
    pass_texture = in_texture;
}
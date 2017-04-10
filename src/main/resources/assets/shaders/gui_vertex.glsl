#version 400 core

//input vertex parameters
//vertex position
in vec2 in_position;
//vertex texture coordinates
in vec2 in_texture;

//output texture coordinates
out vec2 texCoords;

//size of the screen when things are rerndered
uniform vec2 viewport;

void main(void){

    gl_Position = vec4(in_position.x / viewport.x, in_position.y / viewport.y, 0.0, 1.0);

    //values passed to openGL to interpoalte
    texCoords = in_texture;
}
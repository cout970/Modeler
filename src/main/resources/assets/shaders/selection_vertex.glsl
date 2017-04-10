#version 400 core

//input vertex parameters
//vertex position
in vec3 in_position;
//vertex texture coordinates
in vec3 in_color;

out vec3 pass_color;

//model-view-projection matrix
uniform mat4 matrixMVP;

void main(void){

    //world position of the vertex given using model coordinates
    vec4 worldPos = vec4(in_position.xyz, 1.0);
    //final position of the vertex using perpective projection
    gl_Position = matrixMVP * worldPos;

    //values passed to openGL to interpoalte
    pass_color = in_color;
}
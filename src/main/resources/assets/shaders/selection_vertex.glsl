#version 400 core

//input vertex parameters
//vertex position
in vec3 in_position;
//vertex texture coordinates
in vec3 in_color;

out vec3 pass_color;

//matrix used to add perspective projection
uniform mat4 projectionMatrix;
//matrix used to move the camera
uniform mat4 viewMatrix;
//matrix used to move the model
uniform mat4 transformationMatrix;

void main(void){

    //world position of the vertex given using model coordinates
    vec4 worldPos = transformationMatrix * vec4(in_position.xyz, 1.0);
    //position of the vertex relative to the camera
    vec4 viewPos = viewMatrix * worldPos;
    //final position of the vertex using perpective projection
    gl_Position = projectionMatrix * viewPos;

    //values passed to openGL to interpoalte
    pass_color = in_color;
}
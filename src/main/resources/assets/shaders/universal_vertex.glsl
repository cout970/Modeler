#version 400 core

// Input vertex parameters

// vertex position
in vec3 in_position;
// vertex texture coordinates
in vec2 in_texture;
// vertex surface normal
in vec3 in_normal;
// vertex color
in vec3 in_color;

// Output parameters to the next shader

out vec3 pass_position;
// vertex texture coordinates
out vec2 pass_texture;
// vertex surface normal
out vec3 pass_normal;
// vertex color
out vec3 pass_color;
// vector from the vertex to the camera
out vec3 toCameraVector;
// vector from the vertex to the light source
out vec3[4] toLightVector;

// Enviroment variables

// matrix for View and Projection
uniform mat4 matrixVP;
// matrix for Model tranformation
uniform mat4 matrixM;
// position of the camera
uniform vec3 cameraPos;

// Light

// position off every light
uniform vec3[4] lightPos;
// number of active lights
uniform int lightCount = 0;
// if lightning is enabled or not
uniform bool useLight;

void main(void){

    // world position of the vertex given using model coordinates
    vec4 worldPos = matrixM * vec4(in_position.xyz, 1.0);

    // final position of the vertex using the view projection matrix
    gl_Position = matrixVP * worldPos;

    // moving values to the next shader
    pass_position = in_position;
    pass_texture = in_texture;
    pass_color = in_color;

    if(useLight){
        pass_normal = in_normal;
        toCameraVector = cameraPos - worldPos.xyz;

        for(int i = 0; i < lightCount; i++){
            toLightVector[i] = lightPos[i] - worldPos.xyz;
        }
    }
}
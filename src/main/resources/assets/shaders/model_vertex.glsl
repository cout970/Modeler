#version 400 core

//input vertex parameters
//vertex position
in vec3 in_position;
//vertex texture coordinates
in vec2 in_texture;
//vertex surface normal
in vec3 in_normal;
//isSelected from 0 to 1
in float in_selected;

//output parameters to the fragment shader
//interpolated texture coordinates
out vec2 pass_texture;
//interpolated surface normal
out vec3 surfaceNormal;
//vector to the ligth A
out vec3 toLightVectorA;
//vector to the ligth B
out vec3 toLightVectorB;
//vector to the camera
out vec3 toCameraVector;
//isSelected
out float selected;

//matrix used to add perspective projection
uniform mat4 projectionMatrix;
//matrix used to move the camera
uniform mat4 viewMatrix;
//matrix used to move the model
uniform mat4 transformationMatrix;
//position of the ligth A
uniform vec3 lightPositionA;
//position of the ligth B
uniform vec3 lightPositionB;

void main(void){

    //world position of the vertex given using model coordinates
    vec4 worldPos = transformationMatrix * vec4(in_position.xyz, 1.0);
    //position of the vertex relative to the camera
    vec4 viewPos = viewMatrix * worldPos;
    //final position of the vertex using perpective projection
    gl_Position = projectionMatrix * viewPos;

    //values passed to openGL to interpoalte
    pass_texture = in_texture;
    surfaceNormal = in_normal;
    selected = in_selected;

    //vectors towards the ligth
    toLightVectorA = lightPositionA - worldPos.xyz;
    toLightVectorB = lightPositionB - worldPos.xyz;

    //TODO optimize this, we only need to compute the inverse of viewMatrix once, not one per vertex
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPos.xyz;
}
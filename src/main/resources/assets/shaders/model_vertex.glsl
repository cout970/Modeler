#version 400 core

//input vertex parameters
//vertex position
in vec3 in_position;
//vertex texture coordinates
in vec2 in_texture;
//vertex surface normal
in vec3 in_normal;

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

//matrix used to add perspective projection
uniform mat4 matrixMVP;
//position of the camera
uniform vec3 cameraPos;
//position of the ligth A
uniform vec3 lightPositionA;
//position of the ligth B
uniform vec3 lightPositionB;

void main(void){

    //world position of the vertex given using model coordinates
    vec4 worldPos = vec4(in_position.xyz, 1.0);
    //final position of the vertex using perpective projection
    gl_Position = matrixMVP * worldPos;

    //values passed to openGL to interpoalte
    pass_texture = in_texture;
    surfaceNormal = in_normal;

    //vectors towards the ligth
    toLightVectorA = lightPositionA - worldPos.xyz;
    toLightVectorB = lightPositionB - worldPos.xyz;

    //TODO optimize this, we only need to compute the inverse of viewMatrix once, not one per vertex
    toCameraVector = cameraPos - worldPos.xyz;
}
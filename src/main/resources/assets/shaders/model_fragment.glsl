#version 400 core

//input

//texture coordinates
in vec2 pass_texture;
//normal of this bit of material
in vec3 surfaceNormal;
//vector to the light A
in vec3 toLightVectorA;
//vector to the light B
in vec3 toLightVectorB;
//vector towards camera
in vec3 toCameraVector;

//output
out vec4 out_color;

//texture
uniform sampler2D textureSampler;

//light
uniform vec3 lightColorA;
uniform vec3 lightColorB;

// material properties
uniform float shineDamper;
uniform float reflectivity;

//used to render non-model stuff
uniform float enableLight;

//ambient light
const float ambient = 0.1;

vec3 getLight(vec3 color, vec3 lcolor, vec3 toLight, vec3 normal, vec3 toCamera);

void main(void){

    //color of the texture pixel
    vec4 color = texture(textureSampler, pass_texture);

    if(!gl_FrontFacing){
        color = vec4(1.0, 0.5, 0.5, 1.0);
    }

    //pixels with less than 0.01 alpha are not rendered
    if(color.w < 0.01) discard;

    //using ligth
    if(enableLight > 0.5) {
        //calculates the final color using only one ligths at time
        vec3 ligthA = getLight(color.xyz, lightColorA, normalize(toLightVectorA), normalize(surfaceNormal), normalize(toCameraVector));
        vec3 ligthB = getLight(color.xyz, lightColorB, normalize(toLightVectorB), normalize(surfaceNormal), normalize(toCameraVector));
        //50% mix of both ligths
        out_color = mix(vec4(ligthA, 1.0), vec4(ligthB, 1.0), 0.5);
    } else {
        out_color = color;
    }
}

/**
 * Apply Phong light model, using only one ligth
 * Args:
 *      color: texture color
 *      lcolor: ligth color
 *      toLight: vector to the light source
 *      normal: suface normal
 *      toCamera: vector to the camera
*/
vec3 getLight(vec3 color, vec3 lcolor, vec3 toLight, vec3 normal, vec3 toCamera){
    //intentity of the light
    vec3 I;
    //ambient light
    I = vec3(ambient, ambient, ambient);
    //diffuse light
    I = I + lcolor * max(0.0, dot(toLight, normal));
    //specular light
    I = I + lcolor * pow(max(0.0, dot(reflect(toLight, normal), toCamera)), shineDamper) * reflectivity * 1.5;

    /* DEBUG
    I = normal;
    return vec3(I.x+0.5, I.y+0.5, I.z+0.5);// very cool effect
    */
    return color * I * 2;//pow(color * I * 2, vec3(1.0/2.2));
}


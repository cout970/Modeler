#version 400 core

// Input pixel parameters

// vertex texture coordinates
in vec2 pass_texture;
// vertex surface normal
in vec3 pass_normal;
// vertex color
in vec3 pass_color;
// vector from the vertex to the camera
in vec3 toCameraVector;
// vector from the vertex to the light source
in vec3[4] toLightVector;

// Output pixel color
out vec4 out_color;

// Textures
uniform sampler2D textureSampler;

// Flags
uniform bool useTexture;
uniform bool useColor;

// Lights

// position off every light
uniform vec3[4] lightColor;
// number of active lights
uniform int lightCount = 0;
// if lightning is enabled or not
uniform bool useLight;

// Material properties
uniform float shineDamper = 1f;
uniform float reflectivity = 0f;

// Ambient light
uniform float ambient = 0.1;

vec3 getLight(vec3 color, vec3 lcolor, vec3 toLight, vec3 normal, vec3 toCamera);

void main(void){

    if(useTexture) {
        out_color = texture(textureSampler, pass_texture);
    } else {
        out_color = vec4(1.0,1.0,1.0,1.0);
    }


    if(useColor) {
        out_color = vec4(out_color.xyz * pass_color, out_color.w);
    }

    if(useLight){

        vec3 normal = normalize(pass_normal);
        vec3 toCamera = normalize(toCameraVector);
        vec3 acumulator = vec3(0.0, 0.0, 0.0);
        vec3 identity = vec3(1.0, 1.0, 1.0);

        for(int i = 0; i < lightCount; i++){
            acumulator = acumulator + getLight(identity, lightColor[i], normalize(toLightVector[i]), normal, toCamera);
        }

        out_color = vec4(out_color.xyz * (acumulator / lightCount), out_color.w);
    }

    // if the face is not visible then paint it red
    if(!gl_FrontFacing){
        out_color = vec4(1.0, 0.5, 0.5, 1.0);
    }

    // pixels with less than 0.01 alpha are not rendered
    if(out_color.w < 0.01) discard;
}

/**
 * Apply Phong light model, using only one light
 * Args:
 *      color: base color
 *      lcolor: light color
 *      toLight: vector to the light source
 *      normal: suface normal
 *      toCamera: vector to the camera
*/
vec3 getLight(vec3 color, vec3 lcolor, vec3 toLight, vec3 normal, vec3 toCamera){
    // intensity of the light
    vec3 I;
    // ambient light
    I = vec3(ambient, ambient, ambient);
    // diffuse light
    I = I + lcolor * max(0.0, dot(toLight, normal));
    // specular light
    I = I + lcolor * pow(max(0.0, dot(reflect(toLight, normal), toCamera)), shineDamper) * reflectivity * 1.5;

    /* DEBUG
    I = normal;
    return vec3(I.x+0.5, I.y+0.5, I.z+0.5);// very cool effect
    */
    return color * I * 2; //pow(color * I * 2, vec3(1.0/2.2));
}


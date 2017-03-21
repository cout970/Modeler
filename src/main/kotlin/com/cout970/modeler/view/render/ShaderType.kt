package com.cout970.modeler.view.render

/**
 * Created by cout970 on 2017/03/19.
 */
enum class ShaderType {
    FULL_SHADER, //Has 2 lights and is 3D
    PLAIN_3D_SHADER, //Has no texture (only color) and is 3D
    PLAIN_2D_SHADER, // Has texture and is 2D
    UV_SHADER // Has both texture and color, is 3D and has transparency
}
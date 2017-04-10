package com.cout970.modeler.newView.render

/**
 * Created by cout970 on 2017/03/19.
 */
enum class ShaderType {
    MODEL_SHADER, //Has 2 lights and is 3D
    SELECTION_SHADER, //Has no texture (only color) and is 3D
    GUI_SHADER, // Has texture and is 2D
    UV_SHADER // Has both texture and color, is 3D and has transparency
}
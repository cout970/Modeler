package com.cout970.modeler.view.render.tool

/**
 * Created by cout970 on 2017/05/25.
 */

fun com.cout970.glutilities.structure.GLStateMachine.useBlend(amount: Float, func: () -> Unit) {
    com.cout970.glutilities.structure.GLStateMachine.blend.enable()
    com.cout970.glutilities.structure.GLStateMachine.blendFunc = com.cout970.glutilities.structure.GLStateMachine.BlendFunc.CONSTANT_ALPHA to com.cout970.glutilities.structure.GLStateMachine.BlendFunc.ONE_MINUS_CONSTANT_ALPHA
    org.lwjgl.opengl.GL14.glBlendColor(1f, 1f, 1f, amount)
    func()
    com.cout970.glutilities.structure.GLStateMachine.blendFunc = com.cout970.glutilities.structure.GLStateMachine.BlendFunc.SRC_ALPHA to com.cout970.glutilities.structure.GLStateMachine.BlendFunc.ONE_MINUS_SRC_ALPHA
    com.cout970.glutilities.structure.GLStateMachine.blend.disable()
}
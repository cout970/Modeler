package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import org.lwjgl.opengl.GL14

/**
 * Created by cout970 on 2017/05/25.
 */

fun GLStateMachine.useBlend(amount: Float, func: () -> Unit) {
    blend.enable()
    blendFunc = GLStateMachine.BlendFunc.CONSTANT_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_CONSTANT_ALPHA
    GL14.glBlendColor(1f, 1f, 1f, amount)
    func()
    blendFunc = GLStateMachine.BlendFunc.SRC_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_SRC_ALPHA
    blend.disable()
}
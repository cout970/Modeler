package com.cout970.modeler

import com.cout970.glutilities.structure.GLStateMachine
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    override fun tick() {
        GLStateMachine.clear()
    }

    fun initOpenGl() {
        GLStateMachine.clearColor = Color.CYAN
    }
}
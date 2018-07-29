package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.animation.IAnimation

/**
 * Created by cout970 on 2017/07/17.
 */
class TaskUpdateAnimation(val oldAnimation: IAnimation, val newAnimation: IAnimation) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.updateAnimation(newAnimation)
//        state.gui.listeners.run(state.gui.animator)
    }

    override fun undo(state: Program) {
        state.projectManager.updateAnimation(oldAnimation)
//        state.gui.listeners.onAnimatorChange(state.gui.animator)
    }
}
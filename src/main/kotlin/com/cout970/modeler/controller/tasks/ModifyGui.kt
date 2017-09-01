package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.gui.Gui

/**
 * Created by cout970 on 2017/08/31.
 */
class ModifyGui(val func: (Gui) -> Unit) : ITask {

    override fun run(state: Program) = func(state.gui)
}
package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("debug")
fun onDebug(): ITask {
    Debugger.debug {
        //reload gui

        gui.root.reRender()
        gui.resources.reload(resourceLoader)
        gui.root.loadResources(gui.resources)
    }
    return TaskNone
}

@UseCase("debug.show.profiling")
fun showDebugProfiling(): ITask {
    Debugger.showProfiling = !Debugger.showProfiling
    return TaskNone
}

@UseCase("debug.changeColors")
fun changeDebugColors(): ITask {
    ProfilerDiagram.ProfilerDiagramRenderer.colors = ProfilerDiagram.ProfilerDiagramRenderer.generateColors()
    return TaskNone
}
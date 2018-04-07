package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.render.RenderManager

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("debug")
fun onDebug(modelAccessor: IModelAccessor): ITask {
    Debugger.debug {
        //reload gui

        gui.root.reRender()
        gui.resources.reload(resourceLoader)
        gui.root.loadResources(gui.resources)

        // Test import sistem
//        val properties = ImportProperties(
//                "path/to/file//small_steam_engine.tbl",
//                ImportFormat.TBL,
//                flipUV = false,
//                append = false
//        )
//        taskHistory.processTask(TaskImportModel(projectManager.model, properties))
    }
    return TaskNone
}

@UseCase("debug.toggle.dynamic")
fun toggleDynamicDebug(rm: RenderManager): ITask {
    Debugger.DYNAMIC_DEBUG = !Debugger.DYNAMIC_DEBUG
    rm.guiRenderer.context.isDebugEnabled = Debugger.DYNAMIC_DEBUG
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

@UseCase("debug.gc")
fun forceGC(): ITask {
    System.gc()
    return TaskNone
}
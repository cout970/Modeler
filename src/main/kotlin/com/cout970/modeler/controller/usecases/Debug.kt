package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.gui.CSSTheme
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.gui.leguicomp.key
import com.cout970.modeler.render.RenderManager

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("debug")
private fun onDebug(): ITask {
    Debugger.debug {
        //reload gui

        CSSTheme.loadCss()
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
private fun toggleDynamicDebug(rm: RenderManager): ITask {
    Debugger.DYNAMIC_DEBUG = !Debugger.DYNAMIC_DEBUG
    rm.guiRenderer.context.isDebugEnabled = Debugger.DYNAMIC_DEBUG
    return TaskNone
}

@UseCase("debug.show.profiling")
private fun showDebugProfiling(): ITask {
    Debugger.showProfiling = !Debugger.showProfiling
    return TaskNone
}

@UseCase("debug.changeColors")
private fun changeDebugColors(): ITask {
    ProfilerDiagram.ProfilerDiagramRenderer.colors = ProfilerDiagram.ProfilerDiagramRenderer.generateColors()
    return TaskNone
}

@UseCase("debug.gc")
private fun forceGC(): ITask {
    System.gc()
    return TaskNone
}

@UseCase("debug.print.focused")
private fun printFocusedComp(): ITask {
    Debugger.debug {
        val ctx = renderManager.guiRenderer.context

        ctx.focusedGui?.let { gui ->
            println("${gui::class.java.simpleName}(${gui.key})")
            println(gui)
        }
    }
    return TaskNone
}
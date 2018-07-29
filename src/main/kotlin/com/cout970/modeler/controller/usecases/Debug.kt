package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.gui.CSSTheme
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.gui.leguicomp.key
import com.cout970.modeler.render.RenderManager

/**
 * Created by cout970 on 2017/07/20.
 */

@UseCase("debug")
private fun onDebug(): ITask = ModifyGui {
    Debugger.debug {
        //reload gui

        CSSTheme.loadCss()
        gui.root.reRender()
        gui.resources.reload(resourceLoader)
        gui.root.loadResources(gui.resources)

//        pushNotification("Debug", "This is a debug message that is supposed to be long enough to force an overflow in the event box, even when there will never be messages that long in the program")
        // Test import system
//        val properties = ImportProperties(
//                "path/to/file//small_steam_engine.tbl",
//                ImportFormat.TBL,
//                flipUV = false,
//                append = false
//        )
//        taskHistory.processTask(TaskImportModel(projectManager.model, properties))
    }
}

@UseCase("debug.toggle.dynamic")
private fun toggleDynamicDebug(rm: RenderManager): ITask = ModifyGui {
    Debugger.DYNAMIC_DEBUG = !Debugger.DYNAMIC_DEBUG
    rm.guiRenderer.context.isDebugEnabled = Debugger.DYNAMIC_DEBUG
}

@UseCase("debug.show.profiling")
private fun showDebugProfiling(): ITask = ModifyGui {
    Debugger.showProfiling = !Debugger.showProfiling
}

@UseCase("debug.changeColors")
private fun changeDebugColors(): ITask = ModifyGui {
    ProfilerDiagram.ProfilerDiagramRenderer.colors = ProfilerDiagram.ProfilerDiagramRenderer.generateColors()
}

@UseCase("debug.gc")
private fun forceGC(): ITask = ModifyGui {
    System.gc()
}

@UseCase("debug.print.focused")
private fun printFocusedComp(): ITask = ModifyGui {
    Debugger.debug {
        val ctx = renderManager.guiRenderer.context

        ctx.focusedGui?.let { gui ->
            println("${gui::class.java.simpleName}(${gui.key})")
            println(gui)
        }
    }
}
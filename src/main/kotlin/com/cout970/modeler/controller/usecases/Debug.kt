package com.cout970.modeler.controller.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.Program
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone

/**
 * Created by cout970 on 2017/07/20.
 */

class Debug : IUseCase {
    override val key: String = "debug"

    lateinit var state: Program

    override fun createTask(): ITask {
        Debugger.debug {

            //            //reload gui

            gui.editorPanel.reRender()
            gui.resources.reload(resourceLoader)
            gui.root.loadResources(gui.resources)
//
            //Test import
//            val prop = ImportProperties(
//                    "./electric_sieve.tcn",
//                    ImportFormat.TCN,
//                    false
//            )
//            taskHistory.processTask(TaskImportModel(projectManager.model, prop))
        }
        return TaskNone
    }
}

class ShowDebug : IUseCase {
    override val key: String = "debug.show.profiling"

    override fun createTask(): ITask {
        Debugger.showProfiling = !Debugger.showProfiling
        return TaskNone
    }
}
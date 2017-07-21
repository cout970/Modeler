package com.cout970.modeler.functional.usecases

import com.cout970.modeler.Debugger
import com.cout970.modeler.ProgramState
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskImportModel
import com.cout970.modeler.functional.tasks.TaskNone

/**
 * Created by cout970 on 2017/07/20.
 */

class Debug : IUseCase {
    override val key: String = "debug"

    lateinit var state: ProgramState

    override fun createTask(): ITask {
        Debugger.debug {

            //            //reload gui
//            gui.editorPanel = EditorPanel()
//            gui.root.mainPanel = gui.editorPanel
//            gui.guiUpdater.initGui(gui)
//            gui.root.updateSizes(gui.root.size.toIVector())
//
            //Test import
            val prop = ImportProperties(
                    "./electric_sieve.tcn",
                    ImportFormat.TCN,
                    false
            )
            taskHistory.processTask(TaskImportModel(projectManager.model, prop))
        }
        return TaskNone
    }
}